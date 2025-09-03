package com.ecommerce.sb_ecom.mapper;


import com.ecommerce.sb_ecom.model.Category;
import com.ecommerce.sb_ecom.model.Product;
import com.ecommerce.sb_ecom.payload.CategoryDTO;
import com.ecommerce.sb_ecom.payload.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductDTO prodToDTO(Product product);

    Product prodDTOToProd(ProductDTO productDTO);
}
