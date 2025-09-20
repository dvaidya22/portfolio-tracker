import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, Row, Alert, Card, CardBody, CardHeader } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, isNumber, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import axios from 'axios';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities as getPortfolios } from 'app/entities/portfolio/portfolio.reducer';
import { createEntity, getEntity, reset, updateEntity } from './asset.reducer';

export const AssetUpdate = () => {
  const dispatch = useAppDispatch();
  const [stockPrice, setStockPrice] = React.useState(null);
  const [priceLoading, setPriceLoading] = React.useState(false);
  const [tickerInput, setTickerInput] = React.useState('');

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const portfolios = useAppSelector(state => state.portfolio.entities);
  const assetEntity = useAppSelector(state => state.asset.entity);
  const loading = useAppSelector(state => state.asset.loading);
  const updating = useAppSelector(state => state.asset.updating);
  const updateSuccess = useAppSelector(state => state.asset.updateSuccess);

  const handleClose = () => {
    navigate(`/asset${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getPortfolios({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const fetchStockPrice = async (ticker) => {
    if (!ticker || ticker.length < 1) return;
    
    setPriceLoading(true);
    try {
      const response = await axios.get(`/api/portfolio-analytics/stock/${ticker.toUpperCase()}/price`);
      setStockPrice(response.data);
    } catch (error) {
      console.error('Error fetching stock price:', error);
      setStockPrice(null);
    } finally {
      setPriceLoading(false);
    }
  };

  const handleTickerChange = (event) => {
    const value = event.target.value.toUpperCase();
    setTickerInput(value);
    
    // Fetch price when ticker is valid (debounced)
    if (value.length >= 1) {
      const timeoutId = setTimeout(() => {
        fetchStockPrice(value);
      }, 500);
      
      return () => clearTimeout(timeoutId);
    }
  };

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    if (values.quantity !== undefined && typeof values.quantity !== 'number') {
      values.quantity = Number(values.quantity);
    }
    if (values.avgPrice !== undefined && typeof values.avgPrice !== 'number') {
      values.avgPrice = Number(values.avgPrice);
    }

    const entity = {
      ...assetEntity,
      ...values,
      portfolio: portfolios.find(it => it.id.toString() === values.portfolio?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {}
      : {
          ...assetEntity,
          portfolio: assetEntity?.portfolio?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <Card>
            <CardHeader>
              <h2 className="mb-0">
                {isNew ? 'Add New Asset' : 'Edit Asset'}
              </h2>
            </CardHeader>
            <CardBody>
              {loading ? (
                <div className="text-center">
                  <FontAwesomeIcon icon="sync" spin /> Loading...
                </div>
              ) : (
                <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
                  {!isNew ? (
                    <ValidatedField
                      name="id"
                      required
                      readOnly
                      id="asset-id"
                      label={translate('global.field.id')}
                      validate={{ required: true }}
                    />
                  ) : null}
                  
                  <ValidatedField
                    label="Stock Ticker Symbol"
                    id="asset-ticker"
                    name="ticker"
                    data-cy="ticker"
                    type="text"
                    placeholder="e.g., AAPL, MSFT, GOOGL"
                    validate={{
                      required: { value: true, message: translate('entity.validation.required') },
                      minLength: { value: 1, message: translate('entity.validation.minlength', { min: 1 }) },
                      maxLength: { value: 10, message: translate('entity.validation.maxlength', { max: 10 }) },
                    }}
                    onChange={handleTickerChange}
                  />
                  
                  {/* Stock Price Display */}
                  {tickerInput && (
                    <div className="mb-3">
                      {priceLoading ? (
                        <Alert color="info">
                          <FontAwesomeIcon icon="sync" spin /> Fetching current price for {tickerInput}...
                        </Alert>
                      ) : stockPrice ? (
                        <Alert color="success">
                          <strong>{stockPrice.ticker}</strong> - Current Price: <strong>${stockPrice.price}</strong>
                        </Alert>
                      ) : tickerInput.length >= 1 && (
                        <Alert color="warning">
                          Unable to fetch price for {tickerInput}. Please verify the ticker symbol.
                        </Alert>
                      )}
                    </div>
                  )}
                  
                  <ValidatedField
                    label="Quantity"
                    id="asset-quantity"
                    name="quantity"
                    data-cy="quantity"
                    type="number"
                    placeholder="Number of shares"
                    validate={{
                      required: { value: true, message: translate('entity.validation.required') },
                      min: { value: 1, message: translate('entity.validation.min', { min: 1 }) },
                      validate: v => isNumber(v) || translate('entity.validation.number'),
                    }}
                  />
                  
                  <ValidatedField
                    label="Average Purchase Price"
                    id="asset-avgPrice"
                    name="avgPrice"
                    data-cy="avgPrice"
                    type="number"
                    step="0.01"
                    placeholder="Price per share when purchased"
                    validate={{
                      required: { value: true, message: translate('entity.validation.required') },
                      validate: v => isNumber(v) || translate('entity.validation.number'),
                    }}
                  />
                  
                  <ValidatedField
                    id="asset-portfolio"
                    name="portfolio"
                    data-cy="portfolio"
                    label="Portfolio"
                    type="select"
                  >
                    <option value="" key="0">Select a portfolio...</option>
                    {portfolios
                      ? portfolios.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.name}
                          </option>
                        ))
                      : null}
                  </ValidatedField>
                  
                  <div className="d-flex justify-content-between">
                    <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/asset" replace color="secondary">
                      <FontAwesomeIcon icon="arrow-left" />
                      &nbsp; Cancel
                    </Button>
                    <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                      <FontAwesomeIcon icon="save" />
                      &nbsp; {isNew ? 'Add Asset' : 'Update Asset'}
                    </Button>
                  </div>
                </ValidatedForm>
              )}
            </CardBody>
          </Card>
        </Col>
      </Row>
    </div>
  );
};

export default AssetUpdate;
