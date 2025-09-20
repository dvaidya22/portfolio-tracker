import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './user-account.reducer';

export const UserAccountDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const userAccountEntity = useAppSelector(state => state.userAccount.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userAccountDetailsHeading">
          <Translate contentKey="portfolioTrackerApp.userAccount.detail.title">UserAccount</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userAccountEntity.id}</dd>
          <dt>
            <span id="login">
              <Translate contentKey="portfolioTrackerApp.userAccount.login">Login</Translate>
            </span>
          </dt>
          <dd>{userAccountEntity.login}</dd>
          <dt>
            <span id="email">
              <Translate contentKey="portfolioTrackerApp.userAccount.email">Email</Translate>
            </span>
          </dt>
          <dd>{userAccountEntity.email}</dd>
          <dt>
            <span id="password">
              <Translate contentKey="portfolioTrackerApp.userAccount.password">Password</Translate>
            </span>
          </dt>
          <dd>{userAccountEntity.password}</dd>
        </dl>
        <Button tag={Link} to="/user-account" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-account/${userAccountEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default UserAccountDetail;
