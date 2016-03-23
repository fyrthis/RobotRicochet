if [[ $# < 1 ]]; then
	#statements
	echo "arg : number of clients, if you don't want to launch any client, please use 0"
	exit 0
fi

# compile *.c files
./C/make
# compile *.java files
javac -verbose $(find ./src/* | grep .java)


# launch server
./C/server

#We suppose here server takes 5 seconds to be ready
sleep 5

# launch N clients

NB_CLIENTS = $1
for (( i = 0; i < $NB_CLIENTS; i++ )); do
	#statements
	java ./bin/launcher/Launcher
done
