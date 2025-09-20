import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './portfolio.reducer';

export const PortfolioDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const portfolioEntity = useAppSelector(state => state.portfolio.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="portfolioDetailsHeading">
          <Translate contentKey="portfolioTrackerApp.portfolio.detail.title">Portfolio</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{portfolioEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="portfolioTrackerApp.portfolio.name">Name</Translate>
            </span>
          </dt>
          <dd>{portfolioEntity.name}</dd>
          <dt>
            <span id="createdDate">
              <Translate contentKey="portfolioTrackerApp.portfolio.createdDate">Created Date</Translate>
            </span>
          </dt>
          <dd>
            {portfolioEntity.createdDate ? <TextFormat value={portfolioEntity.createdDate} type="date" format={APP_DATE_FORMAT} /> : null}
          </dd>
          <dt>
            <Translate contentKey="portfolioTrackerApp.portfolio.user">User</Translate>
          </dt>
          <dd>{portfolioEntity.user ? portfolioEntity.user.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/portfolio" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/portfolio/${portfolioEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default PortfolioDetail;
