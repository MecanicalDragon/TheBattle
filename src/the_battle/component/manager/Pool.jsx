import React, {Component, Fragment} from 'react';
import {
    Container,
    Jumbotron,
    Button,
    Row,
    Col
} from 'reactstrap'
import {Unit} from "../Unit";
import {FormattedMessage} from 'react-intl';
import AddNew from './addNew';

//TODO: new dropdown here https://reactstrap.github.io/components/dropdowns/
export default class Pool extends Component {
    constructor(props) {
        super(props);
        this.state = {
            pool: this.props.Pool || []
        };
    }

    componentDidUpdate(prevProps) {
        if (this.props.pool !== prevProps.pool) {
            this.setState({pool: this.props.pool});
        }
    }

    render() {
        let {pool} = this.state;
        return (
            <Fragment>
                <h2><FormattedMessage id={"app.manage.pool.header"}/></h2>
                <Row style={{marginBottom: 15, marginLeft: -3}}>
                    {
                        pool.map((unit, index) => {
                            return (
                                <Unit key={index} characteristics={unit} descrFunc={this.props.descrFunc}/>
                            )
                        })
                    }
                    <AddNew addNewHero={this.props.addNewHero}/>
                </Row>
            </Fragment>
        )
    }
}

