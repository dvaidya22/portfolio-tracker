import './home.scss';

import React from 'react';
import { Link } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { Alert, Col, Row, Card, CardBody, CardHeader, Button } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppSelector } from 'app/config/store';

export const Home = () => {
  const account = useAppSelector(state => state.authentication.account);

  return (
    <Row>
      <Col md="12">
        <div className="text-center mb-5">
          <h1 className="display-4 mb-3">
            Welcome to Portfolio Tracker
          </h1>
          <p className="lead">
            Manage your investments with real-time data and AI-powered insights
          </p>
        </div>
        
        {account?.login ? (
          <div>
            <Alert color="success">
              Welcome back, <strong>{account.login}</strong>! Ready to track your portfolio?
            </Alert>
            
            <Row className="mt-4">
              <Col md="4">
                <Card className="h-100">
                  <CardHeader>
                    <FontAwesomeIcon icon="chart-pie" className="me-2" />
                    Portfolio Management
                  </CardHeader>
                  <CardBody>
                    <p>Create and manage multiple investment portfolios with real-time tracking.</p>
                    <Button tag={Link} to="/portfolio" color="primary" block>
                      View Portfolios
                    </Button>
                  </CardBody>
                </Card>
              </Col>
              <Col md="4">
                <Card className="h-100">
                  <CardHeader>
                    <FontAwesomeIcon icon="chart-line" className="me-2" />
                    Asset Tracking
                  </CardHeader>
                  <CardBody>
                    <p>Add stocks and ETFs to your portfolio with automatic price updates.</p>
                    <Button tag={Link} to="/asset" color="success" block>
                      Manage Assets
                    </Button>
                  </CardBody>
                </Card>
              </Col>
              <Col md="4">
                <Card className="h-100">
                  <CardHeader>
                    <FontAwesomeIcon icon="brain" className="me-2" />
                    AI Insights
                  </CardHeader>
                  <CardBody>
                    <p>Get AI-powered recommendations and diversification analysis.</p>
                    <Button tag={Link} to="/portfolio" color="info" block>
                      View Insights
                    </Button>
                  </CardBody>
                </Card>
              </Col>
            </Row>
          </div>
        ) : (
          <div>
            <Alert color="warning">
              Please sign in to access your portfolio dashboard.
            </Alert>
            
            <div className="text-center">
              <Link to="/login" className="alert-link">
                <Button color="primary" size="lg" className="me-3">
                  <FontAwesomeIcon icon="sign-in-alt" /> Sign In
                </Button>
              </Link>
              <Link to="/account/register" className="alert-link">
                <Button color="outline-primary" size="lg">
                  <FontAwesomeIcon icon="user-plus" /> Register
                </Button>
              </Link>
            </div>
            
            <Alert color="info" className="mt-4">
              <strong>Demo Accounts:</strong>
              <br />- Administrator: login="admin", password="admin"
              <br />- User: login="user", password="user"
            </Alert>
          </div>
        )}
      </Col>
    </Row>
  );
};

export default Home;
