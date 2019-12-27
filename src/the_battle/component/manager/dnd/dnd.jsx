import React, {Component} from 'react';
import DragPool from './dragpool'
import styled from "styled-components";


const Container = styled.div`
display: flex;
`;

export default class Dnd extends Component {

    constructor(props){
        super(props);
        this.state= {
            columnOrder: props.dco,
            columns: props.dc,
            heroes: props.dh
        }
    }

    componentDidUpdate(prevProps) {
        if (this.props.dco !== prevProps.dco) {
            this.setState({columnOrder: this.props.dco});
        }
        if (this.props.dc !== prevProps.dc) {
            this.setState({columns: this.props.dc});
        }
        if (this.props.dh !== prevProps.dh) {
            this.setState({heroes: this.props.dh});
        }
    }

    render() {
        return (
                <Container>
                    {this.state.columnOrder.map(columnId => {
                        const column = this.state.columns[columnId];
                        const heroes = column.heroes.map(heroId => this.state.heroes[heroId]);
                        return <DragPool key={column.id} column={column} heroes={heroes}/>
                    })}
                </Container>
        )
    }
}