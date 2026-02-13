set -e

clear
echo "Cleaning output directory..."
rm -rf "out"
mkdir -p "out"

clear
echo "Compiling Java files..."
javac -d "out" $(find "src" -name "*.java")

clear
echo "Running program..."
java -cp "out" "Main"
