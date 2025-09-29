FROM rabbitmq:3.12-management

# Download the delayed message exchange plugin
ADD https://github.com/rabbitmq/rabbitmq-delayed-message-exchange/releases/download/v3.12.0/rabbitmq_delayed_message_exchange-3.12.0.ez /plugins/

# Fix ownership so the 'rabbitmq' user can read and use the plugin file.
RUN chown rabbitmq:rabbitmq /plugins/rabbitmq_delayed_message_exchange-3.12.0.ez

# Now, enable the plugin
RUN rabbitmq-plugins enable --offline rabbitmq_delayed_message_exchange