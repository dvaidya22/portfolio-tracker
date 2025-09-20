import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import UserAccount from './user-account';
import Portfolio from './portfolio';
import Asset from './asset';
/* jhipster-needle-add-route-import - JHipster will add routes here */

export default () => {
  return (
    <div>
      <ErrorBoundaryRoutes>
        {/* prettier-ignore */}
        <Route path="user-account/*" element={<UserAccount />} />
        <Route path="portfolio/*" element={<Portfolio />} />
        <Route path="asset/*" element={<Asset />} />
        {/* jhipster-needle-add-route-path - JHipster will add routes here */}
      </ErrorBoundaryRoutes>
    </div>
  );
};
