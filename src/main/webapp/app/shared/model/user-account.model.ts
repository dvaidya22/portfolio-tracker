export interface IUserAccount {
  id?: number;
  login?: string;
  email?: string;
  password?: string;
}

export const defaultValue: Readonly<IUserAccount> = {};
