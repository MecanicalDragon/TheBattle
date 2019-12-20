import React, {Component} from 'react';
import {connect} from 'react-redux';
import {Container, Jumbotron, Button} from 'reactstrap'
import * as routes from '@/router/routes'
import {setNavPosition} from "@/constants/actions";
import {Home} from "@/constants/paths";
import {FormattedMessage} from 'react-intl';

class Index extends Component {

    constructor(props){
        super(props);
        this.props.dispatch(setNavPosition(Home));
    }

    render() {
        return (
            <Container>
                <Jumbotron>
                    <h1 id={"theBattle"}><FormattedMessage id={'app.index.header'}/></h1>
                    <Button id={"start"} color={"primary"} onClick={() => {
                        this.props.history.push(routes.battle())
                    }}><FormattedMessage id={'app.index.start'}/></Button>
                </Jumbotron>
            </Container>
        )
    }
}

export default connect()(Index);