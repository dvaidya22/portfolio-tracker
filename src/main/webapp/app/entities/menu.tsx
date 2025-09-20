import React from 'react';
import { Translate } from 'react-jhipster';

import MenuItem from 'app/shared/layout/menus/menu-item';

const EntitiesMenu = () => {
  return (
    <>
      {/* prettier-ignore */}
      <MenuItem icon="asterisk" to="/user-account">
        <Translate contentKey="global.menu.entities.userAccount" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/portfolio">
        <Translate contentKey="global.menu.entities.portfolio" />
      </MenuItem>
      <MenuItem icon="asterisk" to="/asset">
        <Translate contentKey="global.menu.entities.asset" />
      </MenuItem>
      {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
    </>
  );
};

export default EntitiesMenu;
