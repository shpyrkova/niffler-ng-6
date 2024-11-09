package guru.qa.niffler.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.jupiter.annotation.ScreenShotTest;
import guru.qa.niffler.model.allure.ScreenDif;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestExecutionExceptionHandler;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ScreenShotTestExtension implements ParameterResolver, TestExecutionExceptionHandler {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ScreenShotTestExtension.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Base64.Encoder encoder = Base64.getEncoder();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), ScreenShotTest.class) &&
                parameterContext.getParameter().getType().isAssignableFrom(BufferedImage.class);
    }

    @SneakyThrows
    @Override
    public BufferedImage resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        String screenshotPath = extensionContext.getRequiredTestMethod().getAnnotation(ScreenShotTest.class).value();
        return ImageIO.read(new ClassPathResource(screenshotPath).getInputStream());
    }

    @Override
    public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
        // Если разницы между скринами нет, то причина падения теста не в картинке, кидаем исключение c причиной падения.
        // Иначе тесты падают всегда на этом методе, даже если сравнение по скринам прошло, а причина падения в другом.
        if (getDiff() == null) throw throwable;
        ScreenShotTest annotation = context.getRequiredTestMethod().getAnnotation(ScreenShotTest.class);
        if (annotation.rewriteExpected()) {
            String expectedPath = annotation.value();
            saveNewExpected(getActual(), expectedPath);
        }
        try {
            ScreenDif screenDif = new ScreenDif(
                    "data:image/png;base64," + encoder.encodeToString(imageToBytes(getExpected())),
                    "data:image/png;base64," + encoder.encodeToString(imageToBytes(getActual())),
                    "data:image/png;base64," + encoder.encodeToString(imageToBytes(getDiff()))
            );

                Allure.addAttachment(
                        "Screenshot diff",
                        "application/vnd.allure.image.diff",
                        objectMapper.writeValueAsString(screenDif)
                );
            } catch (Exception e) {
                System.err.println("Ошибка при прикреплении Screenshot diff: " + e.getMessage());
            }
        throw throwable;
    }

    public static void setExpected(BufferedImage expected) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("expected", expected);
    }

    public static BufferedImage getExpected() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("expected", BufferedImage.class);
    }

    public static void setActual(BufferedImage actual) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("actual", actual);
    }

    public static BufferedImage getActual() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("actual", BufferedImage.class);
    }

    public static void setDiff(BufferedImage diff) {
        TestMethodContextExtension.context().getStore(NAMESPACE).put("diff", diff);
    }

    public static BufferedImage getDiff() {
        return TestMethodContextExtension.context().getStore(NAMESPACE).get("diff", BufferedImage.class);
    }

    private static byte[] imageToBytes(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            ImageIO.write(image, "png", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void saveNewExpected(BufferedImage image, String filePath) throws IOException {
        ImageIO.write(image, "png", new File("src/test/resources/" + filePath));
    }

}