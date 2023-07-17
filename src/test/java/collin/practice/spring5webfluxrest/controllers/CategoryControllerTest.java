package collin.practice.spring5webfluxrest.controllers;

import collin.practice.spring5webfluxrest.domain.Category;
import collin.practice.spring5webfluxrest.repositories.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.BDDAssumptions.given;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryControllerTest {

    WebTestClient webTestClient;
    CategoryRepository categoryRepository;
    CategoryController categoryController;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        categoryController = new CategoryController(categoryRepository);
        webTestClient = WebTestClient.bindToController(categoryController).build();
    }

    @Test
    void list() {
        BDDMockito.given(categoryRepository.findAll())
                .willReturn(Flux.just(Category.builder().description("Category1").build(),
                        Category.builder().description("Category2").build()));
        webTestClient.get().uri("/api/v1/categories")
                .exchange()
                .expectBodyList(Category.class)
                .hasSize(2);
    }

    @Test
    void getById() {
        Category expected = Category.builder().description("Category2").build();
        BDDMockito.given(categoryRepository.findById("someId"))
                .willReturn(Mono.just(expected));
        webTestClient.get().uri("/api/v1/categories/someId")
                .exchange()
                .expectBody(Category.class)
                .isEqualTo(expected);
    }

    @Test
    void testCreateCategory(){
        BDDMockito.given(categoryRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Category.builder().build()));

        Mono<Category> categoryToSave = Mono.just(Category.builder().description("Category1").build());

        webTestClient.post()
                .uri("/api/v1/categories")
                .body(categoryToSave, Category.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    void testUpdate()
    {
        BDDMockito.given(categoryRepository.save(any(Category.class)));

        Mono<Category> categoryToUpdate = Mono.just(Category.builder().description("CATEGORY").build());

        webTestClient.put()
                .uri("/api/v1/categories/someId")
                .body(categoryToUpdate, Category.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void testPatchNoChanges(){
        BDDMockito.given(categoryRepository.findById(anyString()))
                .willReturn(Mono.just(Category.builder().build()));

        BDDMockito.given(categoryRepository.save(any(Category.class)))
                .willReturn(Mono.just(Category.builder().build()));

        Mono<Category> categoryToUpdate = Mono.just(Category.builder().build());

        webTestClient.patch()
                .uri("/api/v1/categories/someId")
                .body(categoryToUpdate, Category.class)
                .exchange()
                .expectStatus()
                .isOk();
        verify(categoryRepository, never()).save(any());
    }
}