if [[ $# < 1 ]]; then
	#statements
	echo "arg : number of clients, if you don't want to launch any client, please use 0"
	exit 0
fi

# compile *.c files
make -C C/ cleanall
make -C C/
# compile *.java files
javac -d binaries $(find ./src/* | grep .java)


# launch server
valgrind --track-origins=yes ./C/serveur &

#We suppose here server takes 5 seconds to be ready
sleep 5

# launch N clients

NB_CLIENTS=$1
for (( i = 0; i < $NB_CLIENTS; i++ )); do
	#statements
	java -cp ./binaries/ launcher.Launcher &
done
