import { useEffect, useState } from 'react';
import { getTanks, createTank, deleteTank } from '../api/fuelApi';
import type { TankDto, CreateTankRequest } from '../api/fuelApi';

export default function TanksPage() {
  const [tanks, setTanks] = useState<TankDto[]>([]);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState<CreateTankRequest>({ fuelType: '', capacityLitres: 0, currentLevelLitres: 0 });

  const fetchTanks = async () => {
    const res = await getTanks();
    setTanks(res.data);
  };

  useEffect(() => {
    fetchTanks();
    const interval = setInterval(fetchTanks, 3000);
    return () => clearInterval(interval);
  }, []);

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    await createTank(form);
    setForm({ fuelType: '', capacityLitres: 0, currentLevelLitres: 0 });
    setShowForm(false);
    fetchTanks();
  };

  const handleDelete = async (id: number) => {
    if (confirm('Delete this tank?')) {
      await deleteTank(id);
      fetchTanks();
    }
  };

  return (
    <div className="page">
      <div className="page-header">
        <h2>Fuel Tanks</h2>
        <button className="btn btn-primary" onClick={() => setShowForm(!showForm)}>
          {showForm ? 'Cancel' : '+ Add Tank'}
        </button>
      </div>

      {showForm && (
        <form className="form-card" onSubmit={handleCreate}>
          <div className="form-group">
            <label htmlFor="fuelType">Fuel Type</label>
            <input
              id="fuelType"
              type="text"
              value={form.fuelType}
              onChange={e => setForm({ ...form, fuelType: e.target.value })}
              placeholder="e.g. Pb95, Pb98, ON"
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="capacity">Capacity (litres)</label>
            <input
              id="capacity"
              type="number"
              step="0.01"
              value={form.capacityLitres || ''}
              onChange={e => setForm({ ...form, capacityLitres: parseFloat(e.target.value) || 0 })}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="currentLevel">Current Level (litres)</label>
            <input
              id="currentLevel"
              type="number"
              step="0.01"
              value={form.currentLevelLitres || ''}
              onChange={e => setForm({ ...form, currentLevelLitres: parseFloat(e.target.value) || 0 })}
              required
            />
          </div>
          <button type="submit" className="btn btn-primary">Create Tank</button>
        </form>
      )}

      <div className="table-container">
        <table className="data-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Fuel Type</th>
              <th>Capacity</th>
              <th>Current Level</th>
              <th>Fill %</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {tanks.map(tank => (
              <tr key={tank.id}>
                <td>{tank.id}</td>
                <td><span className="fuel-badge">{tank.fuelType}</span></td>
                <td>{tank.capacityLitres} L</td>
                <td>{tank.currentLevelLitres} L</td>
                <td>
                  <div className="inline-bar">
                    <div
                      className={`inline-bar-fill ${tank.fillPercentage < 20 ? 'low' : tank.fillPercentage < 50 ? 'medium' : 'high'}`}
                      style={{ width: `${tank.fillPercentage}%` }}
                    />
                    <span>{tank.fillPercentage}%</span>
                  </div>
                </td>
                <td>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDelete(tank.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
