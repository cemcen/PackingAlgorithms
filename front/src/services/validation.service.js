let language = 'es';

let dict = {
    'es': {
        required: 'Debe ingresar este campo.',
        requiredBooleanValue: 'Debe ingresar este campo.',
        requiredPositive: 'El valor ingresado debe ser positivo.',
        email: "Debe ingresar un email válido.",
        betweenValues: (v1, v2) => ('El valor ingresado debe estar entre ' + v1  + ' y ' + v2),
        atLeastOneSelected: 'Debe seleccionar al menos un item.'
    },
    'en': {
        required: 'You must enter this field.',
        requiredBooleanValue: 'You must enter this field.',
        requiredPositive: 'The value must be positive.',
        email: "You must enter a valid email.",
        betweenValues: (v1, v2) => ('The value must be between ' + v1  + ' and ' + v2),
        atLeastOneSelected: 'Must select at least one item.'
    }
};


export default {
    changeLanguage(l) {
        switch (l) {
            case 'es': case 'es-MX': case 'es-ES':
                language = 'es';
                break;
            case 'en': case 'en-US': case 'en-UK':
                language = 'en';
                break;
            default:
                language = 'es';
        }
    },

    required: text => value => !!value || text || dict[language].required,
    requiredBooleanValue: text => value => value != null || text || dict[language].requiredBooleanValue,
    requiredPositive: text => value => (value != null && value > 0) || text || dict[language].requiredPositive,
    email: text => value => {
        const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return pattern.test(value) || text || dict[language].email;
    },
    validRut: text => value => {
        if (!/^[0-9]+[-|‐]{1}[0-9kK]{1}$/.test( value ))
            return text || 'El rut ingresado no es válido.';
        const tmp 	= value.split('-');
        let digv	= tmp[1];
        let rut 	= tmp[0];
        if ( digv == 'K' ) digv = 'k' ;
        let M=0,S=1;
        for(;rut;rut=Math.floor(rut/10))
            S=(S+rut%10*(9-M++%6))%11;

        return ((S?S-1:'k') == digv) || text || 'El rut ingresado no es válido.';
    },
    atLeastOneSelected: text => value => value.length > 0 || text || dict[language].atLeastOneSelected,
    betweenValues: (value1, value2) => text => value => (value <= value2 && value >= value1)
        || text || dict[language].betweenValues(value1, value2)
    ,
    requiredWhenOtherIsSelected: array => valueIncluded => text => value => !array.includes(valueIncluded)
        || (array.includes(valueIncluded) && !!value) || text || 'Debe ingresar este campo.'
    ,
    requiredWhenEqualThan(equalValue, valueToCompare, text) {
        return value => equalValue !== valueToCompare || (equalValue === valueToCompare && value != null) || text || 'Debe ingresar este campo.';
    },
    dateAfterToday(text) {
        return value => {
            let date = new Date();
            if (value) {
                let dates = value.split("/");
                date = new Date(dates[2], dates[1] - 1, dates[0]);
            }

            return date >= new Date() || text || 'La fecha debe ser posterior a la actual.'
        }
    },
    confirmation(equalValue, text) {
        return value => equalValue === value || text || 'Los campos no coinciden.';
    }
};