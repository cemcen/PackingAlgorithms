export default {
    required: text => value => !!value || text || 'Debe ingresar este campo.',
    requiredBooleanValue: text => value => value != null || text || 'Debe ingresar este campo.',
    email: text => value => {
        const pattern = /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
        return pattern.test(value) || text || "Debe ingresar un email válido.";
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
    atLeastOneSelected: text => value => value.length > 0 || text || 'Debe seleccionar al menos un item.',
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