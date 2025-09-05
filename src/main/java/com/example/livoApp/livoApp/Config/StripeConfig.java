package com.example.livoApp.livoApp.Config;

import com.stripe.Stripe;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {

    public StripeConfig(@Value("${stripe.secreteKey}") String stripeSecreteKey){
        Stripe.apiKey = stripeSecreteKey;
    }
}
