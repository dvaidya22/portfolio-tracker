import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row, Card, CardBody, CardHeader, Table, Badge } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Line, Pie } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  ArcElement,
} from 'chart.js';

import { APP_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './portfolio.reducer';
import axios from 'axios';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
);

export const PortfolioDetail = () => {
  const dispatch = useAppDispatch();
  const [portfolioMetrics, setPortfolioMetrics] = React.useState(null);
  const [loading, setLoading] = React.useState(false);
  const [selectedAsset, setSelectedAsset] = React.useState(null);
  const [historicalData, setHistoricalData] = React.useState(null);

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
    fetchPortfolioMetrics();
  }, []);

  const fetchPortfolioMetrics = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const response = await axios.get(`/api/portfolio-analytics/portfolio/${id}/metrics`);
      setPortfolioMetrics(response.data);
    } catch (error) {
      console.error('Error fetching portfolio metrics:', error);
    } finally {
      setLoading(false);
    }
  };

  const fetchHistoricalData = async (ticker) => {
    try {
      const response = await axios.get(`/api/portfolio-analytics/stock/${ticker}/historical`);
      setHistoricalData(response.data);
      setSelectedAsset(ticker);
    } catch (error) {
      console.error('Error fetching historical data:', error);
    }
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
    }).format(value);
  };

  const formatPercentage = (value) => {
    return `${value >= 0 ? '+' : ''}${Number(value).toFixed(2)}%`;
  };

  const getChartData = () => {
    if (!historicalData?.data) return null;
    
    const data = historicalData.data;
    const dates = Object.keys(data).sort().slice(-30);
    const prices = dates.map(date => parseFloat(data[date]['4. close']));
    
    return {
      labels: dates,
      datasets: [
        {
          label: `${selectedAsset} Price`,
          data: prices,
          borderColor: 'rgb(75, 192, 192)',
          backgroundColor: 'rgba(75, 192, 192, 0.2)',
          tension: 0.1,
        },
      ],
    };
  };

  const getPieChartData = () => {
    if (!portfolioMetrics?.assets) return null;
    
    const assets = portfolioMetrics.assets;
    const labels = assets.map(asset => asset.ticker);
    const data = assets.map(asset => parseFloat(asset.currentValue));
    
    return {
      labels,
      datasets: [
        {
          data,
          backgroundColor: [
            '#FF6384',
            '#36A2EB',
            '#FFCE56',
            '#4BC0C0',
            '#9966FF',
            '#FF9F40',
            '#FF6384',
            '#C9CBCF',
          ],
        },
      ],
    };
  };

  const portfolioEntity = useAppSelector(state => state.portfolio.entity);
  
  return (
    <div>
      <Row className="mb-4">
        <Col md="12">
          <div className="d-flex justify-content-between align-items-center">
            <h2 data-cy="portfolioDetailsHeading">
              Portfolio Dashboard: {portfolioEntity.name}
            </h2>
            <div>
              <Button tag={Link} to="/portfolio" replace color="info" className="me-2">
                <FontAwesomeIcon icon="arrow-left" /> Back
              </Button>
              <Button tag={Link} to={`/portfolio/${portfolioEntity.id}/edit`} replace color="primary">
                <FontAwesomeIcon icon="pencil-alt" /> Edit
              </Button>
            </div>
          </div>
        </Col>
      </Row>

      {loading ? (
        <div className="text-center">
          <FontAwesomeIcon icon="sync" spin /> Loading portfolio data...
        </div>
      ) : portfolioMetrics ? (
        <>
          {/* Portfolio Summary Cards */}
          <Row className="mb-4">
            <Col md="3">
              <Card className="text-center">
                <CardBody>
                  <h5 className="card-title">Total Value</h5>
                  <h3 className="text-primary">{formatCurrency(portfolioMetrics.totalValue)}</h3>
                </CardBody>
              </Card>
            </Col>
            <Col md="3">
              <Card className="text-center">
                <CardBody>
                  <h5 className="card-title">Total Cost</h5>
                  <h3>{formatCurrency(portfolioMetrics.totalCost)}</h3>
                </CardBody>
              </Card>
            </Col>
            <Col md="3">
              <Card className="text-center">
                <CardBody>
                  <h5 className="card-title">Gain/Loss</h5>
                  <h3 className={portfolioMetrics.totalGainLoss >= 0 ? 'text-success' : 'text-danger'}>
                    {formatCurrency(portfolioMetrics.totalGainLoss)}
                  </h3>
                  <small className={portfolioMetrics.totalGainLossPercent >= 0 ? 'text-success' : 'text-danger'}>
                    {formatPercentage(portfolioMetrics.totalGainLossPercent)}
                  </small>
                </CardBody>
              </Card>
            </Col>
            <Col md="3">
              <Card className="text-center">
                <CardBody>
                  <h5 className="card-title">Diversification Score</h5>
                  <h3 className="text-info">{portfolioMetrics.diversificationScore}/100</h3>
                </CardBody>
              </Card>
            </Col>
          </Row>

          {/* AI Recommendation */}
          <Row className="mb-4">
            <Col md="12">
              <Card>
                <CardHeader>
                  <FontAwesomeIcon icon="lightbulb" className="me-2" />
                  AI-Powered Recommendation
                </CardHeader>
                <CardBody>
                  <p className="mb-0">
                    Based on your current portfolio composition, we recommend considering{' '}
                    <Badge color="primary">{portfolioMetrics.recommendedAsset}</Badge> to improve diversification.
                  </p>
                </CardBody>
              </Card>
            </Col>
          </Row>

          <Row>
            {/* Assets Table */}
            <Col md="8">
              <Card>
                <CardHeader>
                  <h5 className="mb-0">Portfolio Holdings</h5>
                </CardHeader>
                <CardBody>
                  {portfolioMetrics.assets && portfolioMetrics.assets.length > 0 ? (
                    <Table responsive hover>
                      <thead>
                        <tr>
                          <th>Ticker</th>
                          <th>Quantity</th>
                          <th>Avg Price</th>
                          <th>Current Price</th>
                          <th>Current Value</th>
                          <th>Gain/Loss</th>
                          <th>Actions</th>
                        </tr>
                      </thead>
                      <tbody>
                        {portfolioMetrics.assets.map((asset, index) => (
                          <tr key={index}>
                            <td>
                              <strong>{asset.ticker}</strong>
                            </td>
                            <td>{asset.quantity}</td>
                            <td>{formatCurrency(asset.avgPrice)}</td>
                            <td>{formatCurrency(asset.currentPrice)}</td>
                            <td>{formatCurrency(asset.currentValue)}</td>
                            <td className={asset.gainLoss >= 0 ? 'text-success' : 'text-danger'}>
                              {formatCurrency(asset.gainLoss)}
                              <br />
                              <small>{formatPercentage(asset.gainLossPercent)}</small>
                            </td>
                            <td>
                              <Button
                                size="sm"
                                color="outline-primary"
                                onClick={() => fetchHistoricalData(asset.ticker)}
                              >
                                <FontAwesomeIcon icon="chart-line" /> Chart
                              </Button>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </Table>
                  ) : (
                    <div className="text-center py-4">
                      <p>No assets in this portfolio yet.</p>
                      <Button tag={Link} to="/asset/new" color="primary">
                        <FontAwesomeIcon icon="plus" /> Add First Asset
                      </Button>
                    </div>
                  )}
                </CardBody>
              </Card>
            </Col>

            {/* Portfolio Allocation Chart */}
            <Col md="4">
              <Card>
                <CardHeader>
                  <h5 className="mb-0">Asset Allocation</h5>
                </CardHeader>
                <CardBody>
                  {portfolioMetrics.assets && portfolioMetrics.assets.length > 0 ? (
                    <div style={{ height: '300px' }}>
                      <Pie
                        data={getPieChartData()}
                        options={{
                          responsive: true,
                          maintainAspectRatio: false,
                          plugins: {
                            legend: {
                              position: 'bottom',
                            },
                          },
                        }}
                      />
                    </div>
                  ) : (
                    <div className="text-center py-4">
                      <p>No data to display</p>
                    </div>
                  )}
                </CardBody>
              </Card>
            </Col>
          </Row>

          {/* Historical Price Chart */}
          {selectedAsset && historicalData && (
            <Row className="mt-4">
              <Col md="12">
                <Card>
                  <CardHeader>
                    <h5 className="mb-0">
                      {selectedAsset} - Historical Price (30 Days)
                    </h5>
                  </CardHeader>
                  <CardBody>
                    <div style={{ height: '400px' }}>
                      <Line
                        data={getChartData()}
                        options={{
                          responsive: true,
                          maintainAspectRatio: false,
                          plugins: {
                            legend: {
                              position: 'top',
                            },
                            title: {
                              display: true,
                              text: `${selectedAsset} Price Movement`,
                            },
                          },
                          scales: {
                            y: {
                              beginAtZero: false,
                            },
                          },
                        }}
                      />
                    </div>
                  </CardBody>
                </Card>
              </Col>
            </Row>
          )}
        </>
      ) : (
        <Card>
          <CardBody>
            <p>Loading portfolio metrics...</p>
          </CardBody>
        </Card>
      )}
    </div>
  );
};

export default PortfolioDetail;
