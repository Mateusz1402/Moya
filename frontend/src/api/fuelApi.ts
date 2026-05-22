import api from './client';

export interface TankDto {
  id: number;
  fuelType: string;
  capacityLitres: number;
  currentLevelLitres: number;
  fillPercentage: number;
}

export interface PumpHoseDto {
  id: number;
  pumpId: number;
  tankId: number;
  fuelType: string;
  pricePerLiter: number;
}

export interface PumpDto {
  id: number;
  pumpStatus: string;
  hoses: PumpHoseDto[];
}

export interface DispenseFuelRequest {
  pumpId: number;
  hoseId: number;
  litres: number;
}

export interface DispenseFuelResponse {
  pumpId: number;
  hoseId: number;
  fuelType: string;
  litresRequested: number;
  pricePerLiter: number;
  estimatedCost: number;
  estimatedDurationSeconds: number;
  status: string;
}

export interface CreateTankRequest {
  fuelType: string;
  capacityLitres: number;
  currentLevelLitres: number;
}

export interface CreatePumpRequest {
  pumpStatus: string;
}

export interface CreatePumpHoseRequest {
  pumpId: number;
  tankId: number;
  fuelType: string;
  pricePerLiter: number;
}

// Tanks
export const getTanks = () => api.get<TankDto[]>('/fuel/tanks');
export const getTank = (id: number) => api.get<TankDto>(`/fuel/tanks/${id}`);
export const createTank = (data: CreateTankRequest) => api.post<TankDto>('/fuel/tanks', data);
export const updateTank = (id: number, data: CreateTankRequest) => api.put<TankDto>(`/fuel/tanks/${id}`, data);
export const deleteTank = (id: number) => api.delete(`/fuel/tanks/${id}`);

// Pumps
export const getPumps = () => api.get<PumpDto[]>('/fuel/pumps');
export const getPump = (id: number) => api.get<PumpDto>(`/fuel/pumps/${id}`);
export const createPump = (data: CreatePumpRequest) => api.post<PumpDto>('/fuel/pumps', data);
export const updatePumpStatus = (id: number, status: string) =>
  api.patch<PumpDto>(`/fuel/pumps/${id}/status`, { status });
export const deletePump = (id: number) => api.delete(`/fuel/pumps/${id}`);

// Hoses
export const getHosesByPump = (pumpId: number) => api.get<PumpHoseDto[]>(`/fuel/pumps/${pumpId}/hoses`);
export const createPumpHose = (data: CreatePumpHoseRequest) => api.post<PumpHoseDto>('/fuel/hoses', data);
export const deletePumpHose = (id: number) => api.delete(`/fuel/hoses/${id}`);

// Dispensing
export const dispenseFuel = (data: DispenseFuelRequest) =>
  api.post<DispenseFuelResponse>('/fuel/dispense', data);
