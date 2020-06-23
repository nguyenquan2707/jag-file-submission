package ca.bc.gov.open.jag.efilingaccountclient.config;

import ca.bc.gov.ag.csows.accounts.AccountFacadeBean;
import ca.bc.gov.open.jag.efilingaccountclient.CsoAccountServiceImpl;
import ca.bc.gov.open.jag.efilingaccountclient.EfilingAccountService;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
@EnableConfigurationProperties(CsoAccountProperties.class)
public class AutoConfiguration {

    private final CsoAccountProperties csoAccountProperties;

    public AutoConfiguration(CsoAccountProperties csoAccountProperties) {

        this.csoAccountProperties = csoAccountProperties;
    }

    @Bean
    public AccountFacadeBean accountFacadeBean() {
        JaxWsProxyFactoryBean jaxWsProxyFactoryBean =
                new JaxWsProxyFactoryBean();
        jaxWsProxyFactoryBean.setServiceClass(AccountFacadeBean.class);
        jaxWsProxyFactoryBean.setAddress(csoAccountProperties.getFilingAccountSoapUri());
        jaxWsProxyFactoryBean.setUsername(csoAccountProperties.getUserName());
        jaxWsProxyFactoryBean.setPassword(csoAccountProperties.getPassword());
        return (AccountFacadeBean) jaxWsProxyFactoryBean.create();
    }

    @Bean
    @ConditionalOnMissingBean({EfilingAccountService.class})
    public EfilingAccountService efilingAccountService(AccountFacadeBean accountFacadeBean) {
        return new CsoAccountServiceImpl(accountFacadeBean);
    }


}
