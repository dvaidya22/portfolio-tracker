import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table, Card, CardBody, CardHeader, Badge } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './portfolio.reducer';

export const Portfolio = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const portfolioList = useAppSelector(state => state.portfolio.entities);
  const loading = useAppSelector(state => state.portfolio.loading);
  const totalItems = useAppSelector(state => state.portfolio.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <Card>
        <CardHeader>
          <div className="d-flex justify-content-between align-items-center">
            <h2 className="mb-0">
              <FontAwesomeIcon icon="chart-pie" className="me-2" />
              My Portfolios
            </h2>
            <div>
              <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
                <FontAwesomeIcon icon="sync" spin={loading} /> Refresh
              </Button>
              <Link to="/portfolio/new" className="btn btn-primary">
                <FontAwesomeIcon icon="plus" /> Create Portfolio
              </Link>
            </div>
          </div>
        </CardHeader>
        <CardBody>
          <div className="table-responsive">
            {portfolioList && portfolioList.length > 0 ? (
              <Table responsive hover>
                <thead>
                  <tr>
                    <th className="hand" onClick={sort('name')}>
                      Portfolio Name <FontAwesomeIcon icon={getSortIconByFieldName('name')} />
                    </th>
                    <th className="hand" onClick={sort('createdDate')}>
                      Created Date <FontAwesomeIcon icon={getSortIconByFieldName('createdDate')} />
                    </th>
                    <th>Owner</th>
                    <th>Status</th>
                    <th className="text-end">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {portfolioList.map((portfolio, i) => (
                    <tr key={`entity-${i}`} data-cy="entityTable">
                      <td>
                        <strong>{portfolio.name}</strong>
                      </td>
                      <td>
                        {portfolio.createdDate ? <TextFormat type="date" value={portfolio.createdDate} format={APP_DATE_FORMAT} /> : null}
                      </td>
                      <td>
                        <Badge color="secondary">
                          {portfolio.user ? portfolio.user.login : 'Unknown'}
                        </Badge>
                      </td>
                      <td>
                        <Badge color="success">Active</Badge>
                      </td>
                      <td className="text-end">
                        <div className="btn-group">
                          <Button tag={Link} to={`/portfolio/${portfolio.id}`} color="primary" size="sm">
                            <FontAwesomeIcon icon="chart-line" /> Dashboard
                          </Button>
                          <Button
                            tag={Link}
                            to={`/portfolio/${portfolio.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                            color="outline-primary"
                            size="sm"
                          >
                            <FontAwesomeIcon icon="pencil-alt" />
                          </Button>
                          <Button
                            onClick={() =>
                              (window.location.href = `/portfolio/${portfolio.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                            }
                            color="outline-danger"
                            size="sm"
                          >
                            <FontAwesomeIcon icon="trash" />
                          </Button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </Table>
            ) : (
              !loading && (
                <div className="text-center py-5">
                  <FontAwesomeIcon icon="chart-pie" size="3x" className="text-muted mb-3" />
                  <h4>No Portfolios Found</h4>
                  <p className="text-muted">Create your first portfolio to start tracking your investments.</p>
                  <Button tag={Link} to="/portfolio/new" color="primary">
                    <FontAwesomeIcon icon="plus" /> Create Your First Portfolio
                  </Button>
                </div>
              )
            )}
          </div>
        </CardBody>
      </Card>
      
      {totalItems ? (
        <div className={portfolioList && portfolioList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default Portfolio;
