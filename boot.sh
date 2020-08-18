cd produto
sudo mvn clean package docker:build
cd ../cliente
sudo mvn clean package docker:build
cd ../food-truck
sudo mvn clean package docker:build
sudo docker-compose down
sudo docker-compose up -d
