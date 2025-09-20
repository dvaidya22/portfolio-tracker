import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './asset.reducer';

export const AssetDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const assetEntity = useAppSelector(state => state.asset.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="assetDetailsHeading">
          <Translate contentKey="portfolioTrackerApp.asset.detail.title">Asset</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{assetEntity.id}</dd>
          <dt>
            <span id="ticker">
              <Translate contentKey="portfolioTrackerApp.asset.ticker">Ticker</Translate>
            </span>
          </dt>
          <dd>{assetEntity.ticker}</dd>
          <dt>
            <span id="quantity">
              <Translate contentKey="portfolioTrackerApp.asset.quantity">Quantity</Translate>
            </span>
          </dt>
          <dd>{assetEntity.quantity}</dd>
          <dt>
            <span id="avgPrice">
              <Translate contentKey="portfolioTrackerApp.asset.avgPrice">Avg Price</Translate>
            </span>
          </dt>
          <dd>{assetEntity.avgPrice}</dd>
          <dt>
            <Translate contentKey="portfolioTrackerApp.asset.portfolio">Portfolio</Translate>
          </dt>
          <dd>{assetEntity.portfolio ? assetEntity.portfolio.name : ''}</dd>
        </dl>
        <Button tag={Link} to="/asset" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/asset/${assetEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default AssetDetail;
