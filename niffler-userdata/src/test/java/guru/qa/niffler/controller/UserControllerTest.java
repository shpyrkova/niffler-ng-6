package guru.qa.niffler.controller;

import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.UserEntity;
import guru.qa.niffler.data.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository usersRepository;

  @Test
  void currentUserEndpoint() throws Exception {
    UserEntity userDataEntity = new UserEntity();
    userDataEntity.setUsername("dima");
    userDataEntity.setCurrency(CurrencyValues.RUB);
    usersRepository.save(userDataEntity);

    mockMvc.perform(get("/internal/users/current")
            .contentType(MediaType.APPLICATION_JSON)
            .param("username", "dima")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value("dima"));
  }

  @Test
  void allUsersEndpoint() throws Exception {
    UserEntity currentUserEntity = new UserEntity();
    currentUserEntity.setUsername("dasha");
    currentUserEntity.setCurrency(CurrencyValues.RUB);
    usersRepository.save(currentUserEntity);

    UserEntity secondUserEntity = new UserEntity();
    secondUserEntity.setUsername("igor");
    secondUserEntity.setCurrency(CurrencyValues.RUB);
    usersRepository.save(secondUserEntity);

    mockMvc.perform(get("/internal/users/all")
                    .contentType(MediaType.APPLICATION_JSON)
                    .param("username", "dasha")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].username").value("igor"));
  }

  @Test
  void updateUserInfoEndpoint() throws Exception {
    UserEntity userEntity = new UserEntity();
    userEntity.setUsername("yana");
    userEntity.setFullname("petrova");
    userEntity.setCurrency(CurrencyValues.EUR);
    usersRepository.save(userEntity);

    String updatedUserJson = """
        {
            "username": "yana",
            "fullname": "petrova-ivanova",
            "currency": "KZT"
        }
        """;

    mockMvc.perform(post("/internal/users/update")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(updatedUserJson)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("yana"))
            .andExpect(jsonPath("$.fullname").value("petrova-ivanova"))
            .andExpect(jsonPath("$.currency").value("KZT"));
  }

}