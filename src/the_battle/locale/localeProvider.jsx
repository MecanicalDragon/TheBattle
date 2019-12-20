import React from 'react';
import {connect} from "react-redux";
import en from 'react-intl/locale-data/en';
import de from 'react-intl/locale-data/de';
import ru from 'react-intl/locale-data/ru';
import {IntlProvider, addLocaleData} from 'react-intl'
import messageEn from './messages-en';
import messageDe from './messages-de';
import messageRu from './messages-ru';

function localeProvider(props) {
    const {locale, children} = props;
    const message = {en: messageEn, de: messageDe, ru: messageRu};
    addLocaleData([...en, ...de, ...ru]);
    return (
        <IntlProvider
            key={locale}
            locale={locale}
            messages={message[locale]}>
            {children}
        </IntlProvider>
    );
}

const mapStateToProps = state => ({
    locale: state.intl.locale
});

export default connect(mapStateToProps)(localeProvider);