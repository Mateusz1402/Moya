import { useEffect, useState } from 'react';
import {
  getPumps, createPump, deletePump, updatePumpStatus,
  dispenseFuel, getTanks, createPumpHose, deletePumpHose,
} from '../api/fuelApi';
import type { PumpDto, TankDto, DispenseFuelResponse } from '../api/fuelApi';

export default function PumpsPage() {
  const [pumps, setPumps] = useState<PumpDto[]>([]);
  const [tanks, setTanks] = useState<TankDto[]>([]);
  const [showCreateForm, setShowCreateForm] = useState(false);
  const [dispenseForm, setDispenseForm] = useState<{ pumpId: number; hoseId: number; litres: number } | null>(null);
  const [dispensing, setDispensing] = useState<DispenseFuelResponse | null>(null);
  const [hoseForm, setHoseForm] = useState<{ pumpId: number; tankId: number; fuelType: string; pricePerLiter: number } | null>(null);

  const fetchData = async () => {
    try {
      const [pumpsRes, tanksRes] = await Promise.all([getPumps(), getTanks()]);
      setPumps(pumpsRes.data);
      setTanks(tanksRes.data);
    } catch (err) {
      console.error('Failed to fetch data', err);
    }
  };

  useEffect(() => {
    fetchData();
    const interval = setInterval(fetchData, 2000);
    return () => clearInterval(interval);
  }, []);

  const handleCreatePump = async () => {
    await createPump({ pumpStatus: 'AVAILABLE' });
    setShowCreateForm(false);
    fetchData();
  };

  const handleDelete = async (id: number) => {
    if (confirm('Delete this pump?')) {
      await deletePump(id);
      fetchData();
    }
  };

  const handleDeleteHose = async (id: number) => {
    if (confirm('Disconnect this hose?')) {
      await deletePumpHose(id);
      fetchData();
    }
  };

  const handleToggleStatus = async (pump: PumpDto) => {
    const newStatus = pump.pumpStatus === 'OUT_OF_SERVICE' ? 'AVAILABLE' : 'OUT_OF_SERVICE';
    await updatePumpStatus(pump.id, newStatus);
    fetchData();
  };

  const handleDispense = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!dispenseForm) return;
    try {
      const res = await dispenseFuel(dispenseForm);
      setDispensing(res.data);
      setDispenseForm(null);
      setTimeout(() => {
        setDispensing(null);
        fetchData();
      }, (res.data.estimatedDurationSeconds + 1) * 1000);
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } };
      alert(error.response?.data?.message || 'Dispensing failed');
    }
  };

  const handleCreateHose = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!hoseForm) return;
    try {
      await createPumpHose(hoseForm);
      setHoseForm(null);
      fetchData();
    } catch (err: unknown) {
      const error = err as { response?: { data?: { message?: string } } };
      alert(error.response?.data?.message || 'Failed to create hose');
    }
  };

  const openHoseForm = (pumpId: number) => {
    const firstTank = tanks[0];
    setHoseForm({
      pumpId,
      tankId: firstTank?.id ?? 0,
      fuelType: firstTank?.fuelType ?? '',
      pricePerLiter: 6.5,
    });
  };

  return (
    <div className="page">
      <div className="page-header">
        <h2>Pumps</h2>
        <button className="btn btn-primary" onClick={() => setShowCreateForm(!showCreateForm)}>
          {showCreateForm ? 'Cancel' : '+ Add Pump'}
        </button>
      </div>

      {showCreateForm && (
        <div className="form-card">
          <p>Create a new pump with AVAILABLE status?</p>
          <button className="btn btn-primary" onClick={handleCreatePump}>Confirm</button>
        </div>
      )}

      {dispensing && (
        <div className="dispensing-notification">
          <div className="dispensing-icon">⛽</div>
          <div className="dispensing-info">
            <strong>Pumping in progress — Pump #{dispensing.pumpId}</strong>
            <p>
              {dispensing.fuelType} · {dispensing.litresRequested}L · ~{dispensing.estimatedDurationSeconds}s remaining
            </p>
            <p className="dispensing-cost">Estimated cost: {dispensing.estimatedCost.toFixed(2)} PLN</p>
            <div className="dispensing-progress">
              <div
                className="dispensing-progress-bar"
                style={{ animationDuration: `${dispensing.estimatedDurationSeconds}s` }}
              />
            </div>
          </div>
        </div>
      )}

      <div className="pumps-grid">
        {pumps.map(pump => (
          <div key={pump.id} className={`pump-detail-card status-${pump.pumpStatus.toLowerCase().replace('_', '-')}`}>
            <div className="pump-detail-header">
              <h3>Pump #{pump.id}</h3>
              <span className={`pump-status-badge ${pump.pumpStatus.toLowerCase().replace('_', '-')}`}>
                {pump.pumpStatus.replace('_', ' ')}
              </span>
            </div>

            <div className="pump-hoses-list">
              <div className="hoses-header">
                <h4>Hoses ({pump.hoses.length})</h4>
                {tanks.length > 0 && (
                  <button className="btn btn-sm btn-primary" onClick={() => openHoseForm(pump.id)}>
                    + Hose
                  </button>
                )}
              </div>
              {pump.hoses.length === 0 && <p className="muted">No hoses connected — add one to enable dispensing</p>}
              {pump.hoses.map(hose => (
                <div key={hose.id} className="hose-item">
                  <span className="fuel-badge">{hose.fuelType}</span>
                  <span className="hose-price">{hose.pricePerLiter} PLN/L</span>
                  {pump.pumpStatus === 'AVAILABLE' && (
                    <button
                      className="btn btn-sm btn-success"
                      onClick={() => setDispenseForm({ pumpId: pump.id, hoseId: hose.id, litres: 20 })}
                    >
                      Dispense
                    </button>
                  )}
                  <button
                    className="btn btn-sm btn-danger"
                    onClick={() => handleDeleteHose(hose.id)}
                    disabled={pump.pumpStatus === 'IN_USE'}
                    title="Disconnect hose"
                  >
                    ✕
                  </button>
                </div>
              ))}
            </div>

            <div className="pump-actions">
              <button
                className={`btn btn-sm ${pump.pumpStatus === 'OUT_OF_SERVICE' ? 'btn-success' : 'btn-warning'}`}
                onClick={() => handleToggleStatus(pump)}
                disabled={pump.pumpStatus === 'IN_USE'}
              >
                {pump.pumpStatus === 'OUT_OF_SERVICE' ? 'Enable' : 'Disable'}
              </button>
              <button
                className="btn btn-sm btn-danger"
                onClick={() => handleDelete(pump.id)}
                disabled={pump.pumpStatus === 'IN_USE'}
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>

      {/* Dispense Modal */}
      {dispenseForm && (
        <div className="modal-overlay" onClick={() => setDispenseForm(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h3>Dispense Fuel</h3>
            <form onSubmit={handleDispense}>
              <div className="form-group">
                <label htmlFor="litres">Litres to dispense</label>
                <input
                  id="litres"
                  type="number"
                  step="0.5"
                  min="0.5"
                  value={dispenseForm.litres}
                  onChange={e => setDispenseForm({ ...dispenseForm, litres: parseFloat(e.target.value) || 0 })}
                  required
                />
              </div>
              <div className="form-group">
                <p className="muted">
                  Pump #{dispenseForm.pumpId} · Hose #{dispenseForm.hoseId}
                </p>
                <p className="muted">
                  Estimated time: ~{Math.max(1, Math.round(dispenseForm.litres))}s
                </p>
              </div>
              <div className="modal-actions">
                <button type="button" className="btn" onClick={() => setDispenseForm(null)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Start Pumping</button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* Connect Hose Modal */}
      {hoseForm && (
        <div className="modal-overlay" onClick={() => setHoseForm(null)}>
          <div className="modal" onClick={e => e.stopPropagation()}>
            <h3>Connect Hose to Pump #{hoseForm.pumpId}</h3>
            <form onSubmit={handleCreateHose}>
              <div className="form-group">
                <label htmlFor="hose-tank">Tank</label>
                <select
                  id="hose-tank"
                  value={hoseForm.tankId}
                  onChange={e => {
                    const tank = tanks.find(t => t.id === Number(e.target.value));
                    setHoseForm({
                      ...hoseForm,
                      tankId: Number(e.target.value),
                      fuelType: tank?.fuelType ?? hoseForm.fuelType,
                    });
                  }}
                  required
                >
                  {tanks.map(tank => (
                    <option key={tank.id} value={tank.id}>
                      {tank.fuelType} (Tank #{tank.id} — {tank.currentLevelLitres}L / {tank.capacityLitres}L)
                    </option>
                  ))}
                </select>
              </div>
              <div className="form-group">
                <label htmlFor="hose-fuel-type">Fuel Type Label</label>
                <input
                  id="hose-fuel-type"
                  type="text"
                  value={hoseForm.fuelType}
                  onChange={e => setHoseForm({ ...hoseForm, fuelType: e.target.value })}
                  required
                />
              </div>
              <div className="form-group">
                <label htmlFor="hose-price">Price per Litre (PLN)</label>
                <input
                  id="hose-price"
                  type="number"
                  step="0.01"
                  min="0.01"
                  value={hoseForm.pricePerLiter}
                  onChange={e => setHoseForm({ ...hoseForm, pricePerLiter: parseFloat(e.target.value) || 0 })}
                  required
                />
              </div>
              <div className="modal-actions">
                <button type="button" className="btn" onClick={() => setHoseForm(null)}>Cancel</button>
                <button type="submit" className="btn btn-primary">Connect Hose</button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
