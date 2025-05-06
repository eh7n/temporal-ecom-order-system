# Temporal.io Sample App - Ecom Order System

## About

This is an example of a simple order submission workflow for an ecommerce store using [Temporal.io](https://temporal.io/). It's just meant as a technical exercise and should not be used as the basis for production code.


## Problem Statement

An ecommerce store wants a reliable way of handling the orchestration of their submit order flow. Upon order submission, they want to be able to ensure the funds are available from the customer. From there, they will then reserve the inventory for the order - updating appropriate product catalog services to ensure the stock availability is propagated to the browse search experience.

However, this particular ecommerce retailer has had issues with fraud. Therefore, they have implemented a Fraud Detection system that takes in the customer information and a device fingerprint that was calculate during this checkout flow. In most cases, a decision is made on the spot on whether or not the order is fraudulent. However, in some cases, a manual review is required. This is done through a separate system and a notification is sent once that order has been actioned. This process can take minutes, hours, or even days.

Last, they're integrating with a legacy OMS API endpoint to handle order and warehouse management. This OMS is known to be problematic and often will fail during order creation due to technical problems - therefore it often requires retries to successfully accept an order. 

A reliable and resilent approach is required to ensure data consistency is maintained across all independent systems / microservices. In addition, appropriate compensating actions should be taken in case the order could not be successfully processed end-to-end.

## Process Flow

Below is process flow of the overall business process that has been modeled using Temporal [Workflows](https://docs.temporal.io/workflows). The reference implementation uses various aspects of the Temporal ecosystem such as [Activities](https://docs.temporal.io/activities), [Signals](https://docs.temporal.io/encyclopedia/workflow-message-passing), and [Sagas](https://docs.temporal.io/evaluate/use-cases-design-patterns#saga). 


![Order Submit Process Flow](./docs/TemporalOrderSubmitFlow.svg "Order Submit Process Flow")


## How to run it

Set up your local development environment per [Set up a local development environment for Temporal and Java](https://learn.temporal.io/getting_started/java/dev_environment/).

Start your Temporal server
```
temporal server start-dev --ui-port 8080
```

Install dependencies

```
mvn install
```

Run the OrderSubmitWorker [Worker](https://docs.temporal.io/workers) to register the worker to the SUBMIT_ORDER_QUEUE

```
mvn exec:java -D"exec.mainClass=io.halvorsen.OrderSubmitWorker"
```

Run SubmitOrderMain to submit a test order through

```
mvn exec:java -D"exec.mainClass=io.halvorsen.SubmitOrderMain"
```


Simulate a Fraud Decision via the Temporal.io [CLI](https://docs.temporal.io/cli) 
```
temporal workflow signal --workflow-id {{WORKFLOW_ID}} --name receiveFinalDecision --input '{\"decision\":\"Accept\"}'
```
