import userAccount from 'app/entities/user-account/user-account.reducer';
import portfolio from 'app/entities/portfolio/portfolio.reducer';
import asset from 'app/entities/asset/asset.reducer';
/* jhipster-needle-add-reducer-import - JHipster will add reducer here */

const entitiesReducers = {
  userAccount,
  portfolio,
  asset,
  /* jhipster-needle-add-reducer-combine - JHipster will add reducer here */
};

export default entitiesReducers;
