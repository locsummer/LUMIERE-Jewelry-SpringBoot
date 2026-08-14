package com.agile.jewelryshop;

import com.agile.jewelryshop.repository.ProductRepository;
import com.agile.jewelryshop.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class JewelryShopApplicationTests {
    @Autowired MockMvc mockMvc;
    @Autowired ProductRepository productRepository;
    @Autowired CategoryRepository categoryRepository;

    @Test
    void homeAndProductsLoadSuccessfully() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("LUMIÈRE Jewelry")));
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Bộ sưu tập hôm nay")))
                .andExpect(content().string(containsString("Nhẫn vàng 18K Halo")));
        org.junit.jupiter.api.Assertions.assertTrue(categoryRepository.count() >= 6);
        org.junit.jupiter.api.Assertions.assertTrue(productRepository.count() >= 18);
    }

    @Test
    void aboutAndAdminRequireExpectedAccess() throws Exception {
        mockMvc.perform(get("/about")).andExpect(status().isOk())
                .andExpect(content().string(containsString("Vẻ đẹp lưu giữ khoảnh khắc")));
        mockMvc.perform(get("/admin")).andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void productSearchPageWorks() throws Exception {
        mockMvc.perform(get("/products").param("q", "Sapphire"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Nhẫn đính đá Sapphire")))
                .andExpect(content().string(not(containsString("Cơm bò"))));
    }
}
