import { useEffect, useState } from 'react';
import { getTanks, getPumps } from '../api/fuelApi';
import type { TankDto, PumpDto } from '../api/fuelApi';

export default function Dashboard() {
  const [tanks, setTanks] = useState<TankDto[]>([]);
  const [pumps, setPumps] = useState<PumpDto[]>([]);



  useEffect(() => {
    let isMounted = true;
    const pollData = async() => {
      try{
        const [tanksRes, pumpsRes] = await Promise.all([getTanks(), getPumps()]);
        if(isMounted){
          setTanks(tanksRes.data);
          setPumps(pumpsRes.data)
        }
      }catch(err){
        console.error("Failed to poll data", err);
      }
    };
    pollData();
    const interval = setInterval(pollData, 2000);
    return () => {
      isMounted = false;
      clearInterval(interval);
    };
  }, []);


  const totalCapacity = tanks.reduce((sum, t) => sum + t.capacityLitres, 0);
  const totalFuel = tanks.reduce((sum, t) => sum + t.currentLevelLitres, 0);
  const activePumps = pumps.filter(p => p.pumpStatus === 'IN_USE').length;

  return (
    <div className="dashboard">
      <h2>Dashboard</h2>

      <div className="stats-grid">
        <div className="stat-card">
          <span className="stat-value">{tanks.length}</span>
          <span className="stat-label">Tanks</span>
        </div>
        <div className="stat-card">
          <span className="stat-value">{totalFuel.toFixed(0)}L</span>
          <span className="stat-label">Total Fuel</span>
        </div>
        <div className="stat-card">
          <span className="stat-value">{totalCapacity > 0 ? ((totalFuel / totalCapacity) * 100).toFixed(1) : 0}%</span>
          <span className="stat-label">Overall Fill</span>
        </div>
        <div className="stat-card">
          <span className="stat-value">{pumps.length}</span>
          <span className="stat-label">Pumps</span>
        </div>
        <div className="stat-card highlight">
          <span className="stat-value">{activePumps}</span>
          <span className="stat-label">Pumping Now</span>
        </div>
      </div>

      <div className="dashboard-sections">
        <section className="section">
          <h3>Tank Levels</h3>
          <div className="tank-bars">
            {tanks.map(tank => (
              <div key={tank.id} className="tank-bar-item">
                <div className="tank-bar-label">
                  <span>{tank.fuelType}</span>
                  <span>{tank.currentLevelLitres}L / {tank.capacityLitres}L</span>
                </div>
                <div className="tank-bar-track">
                  <div
                    className={`tank-bar-fill ${tank.fillPercentage < 20 ? 'low' : tank.fillPercentage < 50 ? 'medium' : 'high'}`}
                    style={{ width: `${tank.fillPercentage}%` }}
                  />
                </div>
                <span className="tank-bar-percent">{tank.fillPercentage}%</span>
              </div>
            ))}
          </div>
        </section>

        <section className="section">
          <h3>Pump Status</h3>
          <div className="pump-status-grid">
            {pumps.map(pump => (
              <div key={pump.id} className={`pump-card status-${pump.pumpStatus.toLowerCase().replace('_', '-')}`}>
                <span className="pump-id">Pump #{pump.id}</span>
                <span className={`pump-status-badge ${pump.pumpStatus.toLowerCase().replace('_', '-')}`}>
                  {pump.pumpStatus.replace('_', ' ')}
                </span>
                <span className="pump-hoses">{pump.hoses.length} hose(s)</span>
              </div>
            ))}
          </div>
        </section>
      </div>
    </div>
  );
}
