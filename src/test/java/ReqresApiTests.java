import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static io.restassured.http.ContentType.JSON;

public class ReqresApiTests {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "https://reqres.in";
    }

    @Test
    @DisplayName("Single user")
    void getSingleUser() {
        given()
                .when()
                .get("/api/users/2")
                .then()
                .statusCode(200)
                .body("data.id", is(2), "data.email", is("janet.weaver@reqres.in"),
                        "data.first_name", is("Janet"), "data.last_name", is("Weaver"),
                        "data.avatar", is("https://reqres.in/img/faces/2-image.jpg"),
                        "support.url", is("https://reqres.in/#support-heading"),
                        "support.text", is("To keep ReqRes free, contributions towards server costs are appreciated!"));
    }

    @Test
    @DisplayName("Create user")
    public void createUser() {
        String data = "{ \"name\": \"morpheus\", \"job\": \"leader\" }";
        Response response = given()
                .contentType(JSON)
                .body(data)
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)
                .extract().response();
        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.path("name").toString()).isEqualTo("morpheus");
        assertThat(response.path("job").toString()).isEqualTo("leader");
    }

    @Test
    @DisplayName("Update user")
    public void updateUserData() {
        String data = "{ \"first_name\": \"morpheus\", \"job\": \"leader\" }";
        Response response = given()
                .contentType(JSON)
                .body(data)
                .when()
                .put("/api/users/2")
                .then()
                .statusCode(200)
                .extract().response();
        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.path("job").toString().equals("leader"));
        assertThat(response.path("first_name").toString().equals("morpheus"));
    }

    @Test
    @DisplayName("Delete user")
    public void deleteUser() {
        Response response = given()
                .when()
                .delete("/api/users/2")
                .then()
                .statusCode(204)
                .extract().response();
        assertThat(response.statusCode()).isEqualTo(204);
    }

    @Test
    @DisplayName("User not found")
    void getUserNotFound() {
        Response response = given()
                .contentType(ContentType.JSON)
                .when()
                .log().all()
                .get("/api/unknown/23")
                .then()
                .statusCode(404)
                .extract().response();
        assertThat(response.statusCode()).isEqualTo(404);
    }

    @Test
    @DisplayName("Register Successful")
    public void registerSuccessful() {
        given()
                .contentType(JSON)
                .body("{\"email\": \"eve.holt@reqres.in\",\"password\": \"pistol\"}")
                .when()
                .post("/api/register")
                .then()
                .statusCode(200)
                .body("id", is(4), "token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    @DisplayName("Login successful")
    public void loginSuccessful() {
        given()
                .contentType(JSON)
                .body("{ \"email\": \"eve.holt@reqres.in\"," +
                        " \"password\": \"cityslicka\" }")
                .when()
                .post("/api/login")
                .then()
                .statusCode(200)
                .body("token", is("QpwL5tke4Pnpja7X4"));
    }

    @Test
    @DisplayName("Delayed response")
    public void delayedResponse() {
        given()
                .when()
                .get("/api/users?delay=3")
                .then()
                .statusCode(200)
                .body("page", is(1),
                        "per_page", is(6),
                        "total", is(12),
                        "total_pages", is(2),
                        "data", notNullValue());
    }
}
