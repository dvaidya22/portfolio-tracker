import { IPortfolio } from 'app/shared/model/portfolio.model';

export interface IAsset {
  id?: number;
  ticker?: string;
  quantity?: number;
  avgPrice?: number;
  portfolio?: IPortfolio | null;
}

export const defaultValue: Readonly<IAsset> = {};
