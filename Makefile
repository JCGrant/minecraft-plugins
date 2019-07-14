build:
	mvn package
	cp target/JCCraft*.jar server/plugins/

run: build
	cd server && ./start.sh
