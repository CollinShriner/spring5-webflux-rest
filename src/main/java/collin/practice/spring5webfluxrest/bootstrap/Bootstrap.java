package collin.practice.spring5webfluxrest.bootstrap;

import collin.practice.spring5webfluxrest.domain.Category;
import collin.practice.spring5webfluxrest.domain.Vendor;
import collin.practice.spring5webfluxrest.repositories.CategoryRepository;
import collin.practice.spring5webfluxrest.repositories.VendorRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Bootstrap implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;

    public Bootstrap(CategoryRepository categoryRepository, VendorRepository vendorRepository) {
        this.categoryRepository = categoryRepository;
        this.vendorRepository = vendorRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if(categoryRepository.count().block() == 0)
        {
            System.out.println("LOADING CATEGORIES");

            categoryRepository.save(Category.builder().description("Fruits").build()).block();
            categoryRepository.save(Category.builder().description("Breads").build()).block();
            categoryRepository.save(Category.builder().description("Eggs").build()).block();
            categoryRepository.save(Category.builder().description("Meats").build()).block();

            System.out.println("LOADED " + categoryRepository.count().block() + " CATEGORIES");

            System.out.println("LOADING VENDORS");

            vendorRepository.save(Vendor.builder()
                            .firstName("Mason")
                            .lastName("Jar")
                            .build()).block();

            vendorRepository.save(Vendor.builder()
                    .firstName("Random")
                    .lastName("Name")
                    .build()).block();

            vendorRepository.save(Vendor.builder()
                    .firstName("Sleepy")
                    .lastName("Tired")
                    .build()).block();

            System.out.println("LOADED " + vendorRepository.count().block() + " VENDORS");
        }
    }
}
