build:
	mvn package
	cp target/JCCraft*.jar server/plugins/JCCraft.jar

run: build
	cd server && ./start.sh
