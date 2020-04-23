import React, {Fragment} from 'react';
import {connect} from "react-redux";
import {FormattedMessage} from 'react-intl';
import {
    Container
} from 'reactstrap';

import {setNavPosition} from '@/constants/actions';
import {paths} from '@/constants/paths'

class BreadCrump extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const {navPosition} = this.props.state;
        let currentPageProperty = paths[navPosition] || {};
        const pathElements = currentPageProperty.pathElements || [];

        return (
            <Container style={{paddingTop: "6px", paddingBottom: 0}}>
                <ol className="breadcrumb" style={{marginBottom: 0}}>
                    {
                        pathElements.map((e, index) => {
                            const prevElement = paths[e];
                            return (
                                <Fragment key={index}>
                                    <li><a href="#" onClick={
                                        (event) => {
                                            event.preventDefault();
                                            this.props.history.push(prevElement.link,
                                                this.props.history.location.state)
                                        }
                                    }><FormattedMessage id={prevElement.formattedId}/></a></li>
                                    {
                                        index === pathElements.length ? null : <span>&nbsp;&gt;&nbsp;</span>
                                    }
                                </Fragment>
                            )
                        })
                    }
                    <li className="active-page"><FormattedMessage id={currentPageProperty.formattedId}/></li>
                </ol>
            </Container>
        );
    }
}

const mapStateToProps = state => {
    return ({
        state: state.navPosition
    })
};

const mapDispatchToProps = dispatch => ({
    setNavPosition: position => dispatch(setNavPosition(position))
});


export default connect(mapStateToProps, mapDispatchToProps)(BreadCrump);