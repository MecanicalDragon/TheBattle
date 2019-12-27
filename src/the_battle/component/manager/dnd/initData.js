const initialData = {
    heroes: {
        'Bob': {id: "Bob", type: 'SAGE', name: 'Bob'},
        'Den': {id: "Den", type: 'RANGER', name: 'Den'},
        'Vergil': {id: "Vergil", type: 'FIGHTER', name: 'Vergil'},
        'Julia': {id: "Julia", type: 'RANGER', name: 'Julia'},
        'Lida': {id: "Lida", type: 'FIGHTER', name: 'Lida'},
        'Ore': {id: "Ore", type: 'FIGHTER', name: 'Ore'},
        'Merlin': {id: "Merlin", type: 'SAGE', name: 'Merlin'},
    },
    columns: {
        'reserve' :{
            id: 'reserve',
            title: 'Reserve',
            heroes: ['Bob', 'Den', 'Vergil', 'Merlin', 'Ore', 'Lida', 'Julia']
        },
        'weakLine' :{
            id: 'weakLine',
            title: 'Weak line',
            heroes: []
        },
        'strongLine' :{
            id: 'strongLine',
            title: 'Strong line',
            heroes: []
        }
    },
    columnOrder: ['reserve', 'weakLine' , 'strongLine']
};

export default initialData