import dayjs from 'dayjs';
import { IUserAccount } from 'app/shared/model/user-account.model';

export interface IPortfolio {
  id?: number;
  name?: string;
  createdDate?: dayjs.Dayjs;
  user?: IUserAccount | null;
}

export const defaultValue: Readonly<IPortfolio> = {};
