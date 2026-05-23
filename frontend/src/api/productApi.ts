import api from './client';

export interface ProductDto {
  id: number;
  sku: string;
  name: string;
  category: string;
  price: number;
  stock_quantity: number;
}

export interface CreateProductRequest {
  sku: string;
  name: string;
  category: string;
  price: number;
  stock_quantity: number;
}

export const getProducts = () => api.get<ProductDto[]>('/product/get-all');
export const getProductById = (id: number) => api.get<ProductDto>(`/product/${id}/get-by-id`);
export const getProductsByCategory = (category: string) =>
  api.get<ProductDto[]>(`/product/${category}/get-by-category`);
export const createProduct = (data: CreateProductRequest) =>
  api.post<ProductDto>('/product/create', data);
export const updateProduct = (id: number, data: CreateProductRequest) =>
  api.put<ProductDto>(`/product/${id}/update`, data);
export const deleteProduct = (id: number) => api.delete(`/product/${id}/delete`);
