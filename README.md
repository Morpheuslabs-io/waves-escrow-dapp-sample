# Waves Escrow dApp sample with waveJ API 

## Pre-requirements:
Install these prerequisites:

1. Install Java SDK 1.8 (https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
2. Install Maven 3.5+ (https://maven.apache.org/download.cgi)

## Step 1. Clone the project
`git clone https://github.com/Morpheuslabs-io/waves-dapp-sample-01`

## Step 2. Update configuration
Open file `src\main\resources\application.properties`
- Update NODE_URL field to your node url
- Update NETWORK_BYTE to correct byte mainnet is W, testnet is T
- Update BUYER_PK with correct private key of address with some waves in it

## Step 3. Run app

```
cd waves-dapp-sample-01
mvn clean spring-boot:run` to run application
```

## Step 4. Check transaction Info
Access `{node_url}/api-docs/index.html#!/transactions/info/{}` and enter your transaction Id to check transaction on network, example:

Request

```
curl -X GET --header 'Accept: application/json' 'https://waves1-20860.morpheuslabs.io/transactions/info/GpdLybwJ9WcbagjBbcQnWt3dZ8XHtr8LtTy3eVjs4BVv'
```

Response

```
{
  "senderPublicKey": "hupnQX3JsxmSFMN3kdD972QHfca3q8p9r36QQet7JLR",
  "amount": 1,
  "fee": 4000000,
  "type": 4,
  "version": 2,
  "attachment": "BJHsqe5yBQVtpDUdYdwvZv",
  "sender": "3FhsjnqyDPyXnqJrrGNB47WrjCGygiEiCto",
  "feeAssetId": null,
  "proofs": [
    "5x4nNAzEkxKrnkPmMS2CA7Jv6dzWm8ZusD4ys3WmLdzz99R9pLZESbjSzQ4GenYS24K4JHHNW2AXDZ4Nsr2nx5hZ",
    "",
    "ZkC6RwGAtLUH1aVPKpXGP6n7dmhNt84ogDSpDigDs2oPgpzoDK1ymD5wGqe1tmbsVCrQHM1iQ8cXWxqmjH737EE"
  ],
  "assetId": null,
  "recipient": "3FTh7CPm6ZPsUCaUCMjZz37vAgddS4JCcci",
  "feeAsset": null,
  "id": "GpdLybwJ9WcbagjBbcQnWt3dZ8XHtr8LtTy3eVjs4BVv",
  "timestamp": 1562563716971,
  "height": 12022
}
```
