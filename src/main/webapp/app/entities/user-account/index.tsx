import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UserAccount from './user-account';
import UserAccountDetail from './user-account-detail';
import UserAccountUpdate from './user-account-update';
import UserAccountDeleteDialog from './user-account-delete-dialog';

const UserAccountRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<UserAccount />} />
    <Route path="new" element={<UserAccountUpdate />} />
    <Route path=":id">
      <Route index element={<UserAccountDetail />} />
      <Route path="edit" element={<UserAccountUpdate />} />
      <Route path="delete" element={<UserAccountDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default UserAccountRoutes;
