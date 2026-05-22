import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Layout from './components/Layout';
import Dashboard from './pages/Dashboard';
import TanksPage from './pages/TanksPage';
import PumpsPage from './pages/PumpsPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Dashboard />} />
          <Route path="tanks" element={<TanksPage />} />
          <Route path="pumps" element={<PumpsPage />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
