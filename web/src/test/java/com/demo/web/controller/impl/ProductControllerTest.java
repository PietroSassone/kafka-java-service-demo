package com.demo.web.controller.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.clearInvocations;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.demo.service.exception.ProductNotFoundException;
import com.demo.service.model.ProductModel;
import com.demo.service.service.ProductService;
import com.demo.web.controller.assembler.ProductModelAssembler;
import com.demo.web.entity.ProductEntity;
import com.demo.web.payload.request.ProductRequest;

public class ProductControllerTest {

    private static final String TEST_PRODUCT_NAME = "amulet of the banana king";
    private static final Double TEST_PRODUCT_PRICE = 1000.0;
    private static final Long TEST_PRODUCT_ID = 1L;
    private static final ProductEntity TEST_PRODUCT = new ProductEntity(TEST_PRODUCT_ID, TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE);
    private static final ProductModel TEST_PRODUCT_MODEL = new ProductModel(TEST_PRODUCT_ID, TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE);

    @Mock
    private ProductService productServiceMock;

    @Mock
    private ProductModelAssembler productModelAssemblerMock;

    private final Function<Long, Link> addApiLinkToProduct = productId -> WebMvcLinkBuilder.linkTo(methodOn(ProductController.class).getProduct(productId)).withSelfRel();

    private ProductController undertest;

    @BeforeClass
    private void setupTest() {
        MockitoAnnotations.openMocks(this);
        TEST_PRODUCT_MODEL.add(addApiLinkToProduct.apply(TEST_PRODUCT_ID));

        undertest = new ProductController(productServiceMock, productModelAssemblerMock);
    }

    @AfterMethod
    private void resetMocks() {
        clearInvocations(productServiceMock);
        reset(productModelAssemblerMock);
    }

    @Test
    public void testCreateProductEndpointShouldCreateProductWithValidProductData() {
        // Given
        final ProductRequest createProductRequest = new ProductRequest(TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE);

        given(productServiceMock.findByProductId(TEST_PRODUCT_ID)).willReturn(Optional.empty());
        given(productServiceMock.saveProduct(any())).willReturn(TEST_PRODUCT);
        given(productModelAssemblerMock.toModel(TEST_PRODUCT)).willReturn(TEST_PRODUCT_MODEL);

        final ResponseEntity<?> expectedResponse = ResponseEntity
            .created(TEST_PRODUCT_MODEL.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(TEST_PRODUCT_MODEL);

        // When
        final ResponseEntity<?> actualResponse = undertest.createProduct(createProductRequest);

        // Then
        assertThat(actualResponse, equalTo(expectedResponse));
        verify(productServiceMock).saveProduct(any());
    }

    @Test(expectedExceptions = DataIntegrityViolationException.class, expectedExceptionsMessageRegExp = "Product already exists.")
    public void testCreateProductEndpointShouldNotCreateProductIfItAlreadyExists() {
        // Given
        given(productServiceMock.findByProductName(TEST_PRODUCT_NAME)).willReturn(Optional.of(TEST_PRODUCT));

        // When
        undertest.createProduct(new ProductRequest(TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE));

        // Then - exception is thrown
        verify(productServiceMock, never()).saveProduct(TEST_PRODUCT);
    }

    @Test
    public void testUpdateProductEndpointShouldCreateProductWhenItDoesNotExist() {
        // Given
        final ProductRequest productRequest = new ProductRequest(TEST_PRODUCT_NAME, TEST_PRODUCT_PRICE);

        given(productServiceMock.findByProductId(TEST_PRODUCT_ID)).willReturn(Optional.empty());
        given(productServiceMock.saveProduct(any())).willReturn(TEST_PRODUCT);
        given(productModelAssemblerMock.toModel(TEST_PRODUCT)).willReturn(TEST_PRODUCT_MODEL);

        final ResponseEntity<?> expectedResponse = ResponseEntity
            .created(TEST_PRODUCT_MODEL.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(TEST_PRODUCT_MODEL);

        // When
        final ResponseEntity<?> actualResponse = undertest.updateProduct(productRequest, TEST_PRODUCT_ID);

        // Then
        assertThat(actualResponse, equalTo(expectedResponse));
        verify(productServiceMock).saveProduct(any());
    }

    @Test
    public void testUpdateProductEndpointShouldUpdateProductIfItAlreadyExists() {
        // Given
        final Double newPrice = 999.9;
        final ProductRequest productRequest = new ProductRequest(TEST_PRODUCT_NAME, newPrice);
        final ProductEntity updatedProduct = new ProductEntity(TEST_PRODUCT_ID, TEST_PRODUCT_NAME, newPrice);
        final ProductModel updatedProductModel = new ProductModel(TEST_PRODUCT_ID, TEST_PRODUCT_NAME, newPrice).add(addApiLinkToProduct.apply(TEST_PRODUCT_ID));

        given(productServiceMock.findByProductId(TEST_PRODUCT_ID)).willReturn(Optional.of(TEST_PRODUCT));
        given(productServiceMock.saveProduct(any())).willReturn(updatedProduct);
        given(productModelAssemblerMock.toModel(updatedProduct)).willReturn(updatedProductModel);

        final ResponseEntity<?> expectedResponse = ResponseEntity
            .created(updatedProductModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(updatedProductModel);

        // When
        final ResponseEntity<?> actualResponse = undertest.updateProduct(productRequest, TEST_PRODUCT_ID);

        // Then
        assertThat(actualResponse, equalTo(expectedResponse));
        verify(productServiceMock).saveProduct(any());
    }

    @Test
    public void testGetProductEndpointShouldReturnProductForExistingProductId() {
        // Given
        given(productServiceMock.findByProductId(TEST_PRODUCT_ID)).willReturn(Optional.of(TEST_PRODUCT));
        given(productModelAssemblerMock.toModel(TEST_PRODUCT)).willReturn(TEST_PRODUCT_MODEL);
        final ResponseEntity<ProductModel> expectedResponse = new ResponseEntity<>(TEST_PRODUCT_MODEL, HttpStatus.OK);

        // When
        final ResponseEntity<ProductModel> actualResponse = undertest.getProduct(TEST_PRODUCT_ID);

        // Then
        assertThat(actualResponse, equalTo(expectedResponse));
    }

    @Test(dataProvider = "negativeCaseProductIdsDataProvider", expectedExceptions = ProductNotFoundException.class, expectedExceptionsMessageRegExp = "Could not find product with id: .*")
    public void testGetProductEndpointShouldThrowProductNotFoundExceptionOnNonexistentProduct(final Long productId) {
        // Given
        given(productServiceMock.findByProductId(productId)).willReturn(Optional.empty());

        // When
        undertest.getProduct(productId);

        // Then - exception is thrown
    }

    @Test
    public void testDeleteProductEndpointShouldDeleteProductOnExistingProductId() {
        // Given
        given(productServiceMock.findByProductId(TEST_PRODUCT_ID)).willReturn(Optional.of(TEST_PRODUCT));

        final ResponseEntity<Void> expectedResponse = ResponseEntity.noContent().build();

        // When
        final ResponseEntity<Void> actualResponse = undertest.deleteProduct(TEST_PRODUCT_ID);

        // Then
        assertThat(actualResponse, equalTo(expectedResponse));
        verify(productServiceMock).deleteById(TEST_PRODUCT_ID);
    }

    @Test(dataProvider = "negativeCaseProductIdsDataProvider", expectedExceptions = ProductNotFoundException.class, expectedExceptionsMessageRegExp = "Could not find product with id: .*")
    public void testDeleteProductEndpointShouldReturnProductNotFoundExceptionOnNonexistentProduct(final Long productId) {
        // Given
        given(productServiceMock.findByProductId(productId)).willReturn(Optional.empty());

        // When
        undertest.deleteProduct(productId);

        // Then - exception is thrown
        verify(productServiceMock, never()).deleteById(TEST_PRODUCT_ID);
    }

    @Test
    public void testGetAllProductsEndpointShouldReturnEmptyResponseWhenNoProductsExist() {
        // Given
        // When
        final ResponseEntity<CollectionModel<ProductModel>> actualResponse = undertest.getAllProducts();

        // Then
        assertThat(actualResponse, equalTo(new ResponseEntity<>(HttpStatus.OK)));
    }

    @Test
    public void testGetAllProductsEndpointShouldReturnAllProducts() {
        // Given
        final List<ProductEntity> allProducts = List.of(TEST_PRODUCT, TEST_PRODUCT);
        final List<ProductModel> allProductModels = List.of(TEST_PRODUCT_MODEL, TEST_PRODUCT_MODEL);
        final CollectionModel<ProductModel> productModelsCollection = CollectionModel.of(allProductModels);

        given(productServiceMock.getAllProducts()).willReturn(allProducts);
        given(productModelAssemblerMock.toCollectionModel(any())).willReturn(productModelsCollection);

        final ResponseEntity<CollectionModel<ProductModel>> expectedResponse = new ResponseEntity<>(productModelsCollection, HttpStatus.OK);

        // When
        final ResponseEntity<CollectionModel<ProductModel>> actualResponse = undertest.getAllProducts();
        // Then
        assertThat(actualResponse, equalTo(expectedResponse));
    }

    @DataProvider
    private Object[][] negativeCaseProductIdsDataProvider() {
        return new Object[][]{
            {0L}, {-1L}, {null}
        };
    }
}
