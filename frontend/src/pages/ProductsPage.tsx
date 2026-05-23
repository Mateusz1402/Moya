import { useEffect, useState } from 'react';
import { getProducts, createProduct, updateProduct, deleteProduct } from '../api/productApi';
import type { ProductDto, CreateProductRequest } from '../api/productApi';
import Dropdown from '../components/Dropdown';
const CATEGORIES = ['WATER', 'BEER', 'HOTDOG', 'SWEET', 'TOBACCO'];

const emptyForm: CreateProductRequest = {
  sku: '',
  name: '',
  category: CATEGORIES[0],
  price: 0,
  stock_quantity: 0,
};

export default function ProductsPage() {
  const [products, setProducts] = useState<ProductDto[]>([]);
  const [filterCategory, setFilterCategory] = useState<string>('ALL');
  const [searchQuery, setSearchQuery] = useState('');
  const [showForm, setShowForm] = useState(false);
  const [editingProduct, setEditingProduct] = useState<ProductDto | null>(null);
  const [form, setForm] = useState<CreateProductRequest>(emptyForm);

  const fetchProducts = async () => {
    try{
      const res = await getProducts();
      setProducts(res.data);
    } catch(err){
      console.error("Failed to fetch products", err);
    }
  };

  useEffect(() => {
    fetchProducts();

  }, []);


  const filteredProducts = products.filter(p => {
    const matchesCategory = filterCategory === 'ALL' || filterCategory === p.category;
    const matchesSearch = searchQuery === '' ||
        p.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        p.sku.toLowerCase().includes(searchQuery.toLowerCase());
    return matchesCategory && matchesSearch;
  })

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (editingProduct) {
        await updateProduct(editingProduct.id, form);
      } else {
        await createProduct(form);
      }
      setForm(emptyForm);
      setShowForm(false);
      setEditingProduct(null);
      fetchProducts();
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } };
      alert(error.response?.data?.message || 'Operation failed');
    }
  };

  const handleEdit = (product: ProductDto) => {
    setEditingProduct(product);
    setForm({
      sku: product.sku,
      name: product.name,
      category: product.category,
      price: product.price,
      stock_quantity: product.stock_quantity,
    });
    setShowForm(true);
  };

  const handleDelete = async (id: number) => {
    if (confirm('Delete this product?')) {
      await deleteProduct(id);
      fetchProducts();
    }
  };

  const handleCancel = () => {
    setShowForm(false);
    setEditingProduct(null);
    setForm(emptyForm);
  };

  const totalStock = products.reduce((sum, p) => sum + p.stock_quantity, 0);
  const totalValue = products.reduce((sum, p) => sum + p.price * p.stock_quantity, 0);

  return (
    <div className="page">
      <div className="page-header">
        <h2>Products</h2>
        <button className="btn btn-primary" onClick={() => { setShowForm(!showForm); setEditingProduct(null); setForm(emptyForm); }}>
          {showForm && !editingProduct ? 'Cancel' : '+ Add Product'}
        </button>
      </div>

      {/* Stats */}
      <div className="stats-grid">
        <div className="stat-card">
          <span className="stat-value">{products.length}</span>
          <span className="stat-label">Products</span>
        </div>
        <div className="stat-card">
          <span className="stat-value">{totalStock} </span>
          <span className="stat-label">Total Stock</span>
        </div>
        <div className="stat-card">
          <span className="stat-value">{totalValue.toFixed(2)}</span>
          <span className="stat-label">Stock Value (PLN)</span>
        </div>
        <div className="stat-card">
          <span className="stat-value">{new Set(products.map(p => p.category)).size}</span>
          <span className="stat-label">Categories</span>
        </div>
      </div>

      {/* Create/Edit Form */}
      {showForm && (
        <form className="form-card" onSubmit={handleSubmit}>
          <h3 style={{ marginBottom: '1rem' }}>{editingProduct ? `Edit: ${editingProduct.name}` : 'New Product'}</h3>
          <div className="product-form-grid">
            <div className="form-group">
              <label htmlFor="sku">SKU</label>
              <input
                id="sku"
                type="text"
                value={form.sku}
                onChange={e => setForm({ ...form, sku: e.target.value })}
                placeholder="e.g. WTR-001"
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="product-name">Name</label>
              <input
                id="product-name"
                type="text"
                value={form.name}
                onChange={e => setForm({ ...form, name: e.target.value })}
                placeholder="e.g. Spring Water 0.5L"
                required
              />
            </div>
            <div className="form-group">
              <label>Category</label>
              <Dropdown
                value={form.category}
                options={CATEGORIES}
                onChange={(val) => setForm({ ...form, category: val })}
              />
            </div>
            <div className="form-group">
              <label htmlFor="product-price">Price (PLN)</label>
              <input
                id="product-price"
                type="number"
                step="0.01"
                min="0.01"
                value={form.price || ''}
                onChange={e => setForm({ ...form, price: parseFloat(e.target.value) || 0 })}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="product-stock">Stock Quantity</label>
              <input
                id="product-stock"
                type="number"
                min="0"
                value={form.stock_quantity || ''}
                onChange={e => setForm({ ...form, stock_quantity: parseInt(e.target.value) || 0 })}
                required
              />
            </div>
          </div>
          <div className="form-actions">
            <button type="button" className="btn" onClick={handleCancel}>Cancel</button>
            <button type="submit" className="btn btn-primary">
              {editingProduct ? 'Save Changes' : 'Create Product'}
            </button>
          </div>
        </form>
      )}

      {/* Filters */}
      <div className="filters-bar">
        <div className="filter-group">
          <label>Category:</label>
          <Dropdown
            value={filterCategory}
            options={['ALL', ...CATEGORIES]}
            onChange={(val) => setFilterCategory(val)}
          />
        </div>
        <div className="filter-group">
          <label htmlFor="filter-search">Search:</label>
          <input
            id="filter-search"
            type="text"
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            placeholder="Name or SKU..."
          />
        </div>
        <span className="muted">{filteredProducts.length} result(s)</span>
      </div>

      {/* Products Table */}
      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>SKU</th>
              <th>Name</th>
              <th>Category</th>
              <th>Price (PLN)</th>
              <th>Stock</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {filteredProducts.length === 0 && (
              <tr>
                <td colSpan={6} style={{ textAlign: 'center', padding: '2rem' }}>
                  <span className="muted">No products found</span>
                </td>
              </tr>
            )}
            {filteredProducts.map(product => (
              <tr key={product.id}>
                <td><code className="sku-code">{product.sku}</code></td>
                <td>{product.name}</td>
                <td><span className={`category-badge cat-${product.category.toLowerCase()}`}>{product.category}</span></td>
                <td>{product.price.toFixed(2)}</td>
                <td>
                  <span className={`stock-indicator ${product.stock_quantity === 0 ? 'out' : product.stock_quantity < 10 ? 'low' : 'ok'}`}>
                    {product.stock_quantity}
                  </span>
                </td>
                <td>
                  <div className="table-actions">
                    <button className="btn btn-sm" onClick={() => handleEdit(product)}>Edit</button>
                    <button className="btn btn-sm btn-danger" onClick={() => handleDelete(product.id)}>Delete</button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
