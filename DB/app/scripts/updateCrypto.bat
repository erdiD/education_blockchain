SET SOURCE_CRYPTO=/opt/gopath/src/github.com/hyperledger/fabric/examples/twoOrgsTwoPeers/crypto-config
SET TARGET_CRYPTO=../chaincode/smartcontract/ilv-fabric-client/artifacts/crypto-config
SET SOURCE_ARTIFACTS=/opt/gopath/src/github.com/hyperledger/fabric/examples/twoOrgsTwoPeers/channel-artifacts
SET TARGET_ARTIFACTS=../chaincode/smartcontract/ilv-fabric-client/artifacts

SET IP=10.43.185.249

rm -Rf %TARGET_ARTIFACTS%/*
scp -r ec2-user@%IP%:%SOURCE_CRYPTO%/* %TARGET_CRYPTO%
scp -r ec2-user@%IP%:%SOURCE_ARTIFACTS%/channel.tx %TARGET_ARTIFACTS%/mychannel.tx
scp -r ec2-user@%IP%:%SOURCE_ARTIFACTS%/genesis.block %TARGET_ARTIFACTS%/orderer.block