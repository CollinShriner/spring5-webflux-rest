package collin.practice.spring5webfluxrest.controllers;

import collin.practice.spring5webfluxrest.domain.Vendor;
import collin.practice.spring5webfluxrest.repositories.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class VendorControllerTest {

    WebTestClient webTestClient;
    private VendorController vendorController;
    private VendorRepository vendorRepository;

    @BeforeEach
    void setUp() {
        vendorRepository=Mockito.mock(VendorRepository.class);
        vendorController = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(vendorController).build();
    }

    @Test
    void list() {
        BDDMockito.given(vendorRepository.findAll())
                .willReturn(Flux.just(Vendor.builder()
                                .firstName("FN1")
                                .lastName("LN1")
                                .build(),
                        Vendor.builder()
                                .firstName("FN2")
                                .lastName("LN2")
                                .build()));
        webTestClient.get().uri("/api/v1/vendors")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    void getById() {
        Vendor expected = Vendor.builder()
                .firstName("FN1")
                .lastName("LN1")
                .build();
        BDDMockito.given(vendorRepository.findById("someId"))
                .willReturn(Mono.just(expected));
        webTestClient.get().uri("/api/v1/vendors/someId")
                .exchange()
                .expectBody(Vendor.class)
                .isEqualTo(expected);
    }

    @Test
    void testCreateCategory(){
        BDDMockito.given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSave = Mono.just(Vendor.builder().firstName("FN1")
                .lastName("LN1").build());

        webTestClient.post()
                .uri("/api/v1/vendors")
                .body(vendorToSave, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    void testUpdate()
    {
        BDDMockito.given(vendorRepository.save(any(Vendor.class)));

        Mono<Vendor> vendorToUpdate = Mono.just(Vendor.builder().firstName("FN1")
                .lastName("LN1").build());

        webTestClient.put()
                .uri("/api/v1/vendors/someId")
                .body(vendorToUpdate, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    void testPatchNoChanges(){
        BDDMockito.given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().build()));

        BDDMockito.given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorToUpdate = Mono.just(Vendor.builder().build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someId")
                .body(vendorToUpdate, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();
        verify(vendorRepository, never()).save(any());
    }
}