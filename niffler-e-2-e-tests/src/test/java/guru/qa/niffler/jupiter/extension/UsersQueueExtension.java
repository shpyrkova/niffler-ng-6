package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    public record StaticUser(
            String username,
            String password,
            String friend,
            String income,
            String outcome) {
    }

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

    static {
        EMPTY_USERS.add(new StaticUser("camel", "00000000", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("giraffe", "00000000", "becker.co", null, null));
        WITH_INCOME_REQUEST_USERS.add(new StaticUser("dasha", "00000000", null, "becker.co", null));
        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("belka", "00000000", null, null, "abshire.com"));
    }

    // создаем аннотацию @UserType, которая и прокидывается в тест
    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UserType {
        Type value() default Type.EMPTY;
        enum Type {
            EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void beforeTestExecution(ExtensionContext context) {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                // фильтруем параметры тестового метода по нужной аннотации
                .filter(p -> AnnotationSupport.isAnnotated(p, UserType.class))
                // для каждого параметра ожидаем свободного юзера из очереди
                .forEach(p -> {
                    UserType ut = p.getAnnotation(UserType.class);
                    Optional<StaticUser> user = Optional.empty();
                    StopWatch sw = StopWatch.createStarted();
                    while (user.isEmpty() && sw.getTime(TimeUnit.SECONDS) < 30) {
                        user = switch (ut.value()) {
                            case EMPTY -> Optional.ofNullable(EMPTY_USERS.poll());
                            case WITH_FRIEND -> Optional.ofNullable(WITH_FRIEND_USERS.poll());
                            case WITH_INCOME_REQUEST -> Optional.ofNullable(WITH_INCOME_REQUEST_USERS.poll());
                            case WITH_OUTCOME_REQUEST -> Optional.ofNullable(WITH_OUTCOME_REQUEST_USERS.poll());
                        };
                        Allure.getLifecycle().updateTestCase(testCase ->
                                testCase.setStart(new Date().getTime())
                        );
                        // кладем юзера в мапу, где ключ - соответствующий тип юзера
                        Map<UserType, StaticUser> userMap = (Map<UserType, StaticUser>) context.getStore(NAMESPACE)
                                .getOrComputeIfAbsent(
                                        context.getUniqueId(),
                                        key -> new HashMap<>()
                                );
                        user.ifPresentOrElse(
                                u -> userMap.put(ut, u),
                                () -> {
                                    throw new IllegalStateException("Can`t obtain user after 30s.");
                                }
                        );
                    }
                });
    }


    @Override
    @SuppressWarnings("unchecked")
    public void afterTestExecution(ExtensionContext context) {
        // достаем из контекста и кладем в мапу юзеров, которые использовались в тесте
        Map<UserType, StaticUser> userMap = context.getStore(NAMESPACE)
                .get(context.getUniqueId(),
                        Map.class);
        // достаем из мапы каждого юзера по ключу и раскладываем по своим очередям
        for (Map.Entry<UserType, StaticUser> e : userMap.entrySet()) {
            switch (e.getKey().value()) {
                case EMPTY -> EMPTY_USERS.add(e.getValue());
                case WITH_FRIEND -> WITH_FRIEND_USERS.add(e.getValue());
                case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS.add(e.getValue());
                case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS.add(e.getValue());
            }
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext context) throws ParameterResolutionException {
        // создаем мапу, в которую кладем уже подготовленного в beforeEach юзера
        Map<UserType, StaticUser> userMap = (Map<UserType, StaticUser>) context.getStore(NAMESPACE).get(context.getUniqueId());
        UserType userTypeAnnotation = parameterContext
                .findAnnotation(UserType.class)
                .orElseThrow(() -> new ParameterResolutionException("@UserType annotation not found"));
        // отдаем в тест данные пользователя по ключу userType, который указан в аннотации
        return userMap.get(userTypeAnnotation);
    }

}