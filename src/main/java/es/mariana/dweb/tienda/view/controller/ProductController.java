/*
 * =============================================================================
 *
 *   Copyright (c) 2011-2026 Thymeleaf (http://www.thymeleaf.org)
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 * =============================================================================
 */
package es.mariana.dweb.tienda.view.controller;

import es.mariana.dweb.tienda.model.entities.Product;
import es.mariana.dweb.tienda.model.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("product/list")
    public String productList(Model model) {

        final List<Product> allProducts = productService.findAll();
        model.addAttribute("prods", allProducts);
        return "product/list";
    }

    @GetMapping("product/comments")
    public String orderList(@RequestParam("prodId") Integer prodId, Model model) {

        final Product product = productService.findById(prodId);
        model.addAttribute("prod", product);
        return "product/comments";
    }

    @GetMapping("product/new")
    public String newProduct(final Model model) {
        return "product/form";
    }

    @GetMapping("product/edit")
    public String editProduct(@RequestParam("prodId") final Integer productId, final Model model) {
        model.addAttribute("product", this.productService.findById(productId));
        return "product/form";
    }

    @PostMapping("product/save")
    public String saveProduct(
            @RequestParam(value = "id", required = false) final Integer id,
            @RequestParam("name") final String name,
            @RequestParam(value = "inStock", required = false) final String inStock,
            @RequestParam("price") final BigDecimal price) {

        if (id == null) this.productService.create(name, inStock != null, price);
        else this.productService.update(id, name, inStock != null, price);
        return "redirect:/product/list";
    }

    @PostMapping("product/delete")
    public String deleteProduct(@RequestParam("prodId") final Integer productId) {
        this.productService.deleteById(productId);
        return "redirect:/product/list";
    }



}
