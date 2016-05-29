sum()
{
	echo "Sum of numbers $1 and $2 is : `expr $1 + $2`"
}

subtract()
{
	echo "Subtraction of numbers $1 and $2 is : `expr $1 - $2`"
}
multiply()
{
	echo "Multiplication of the numbers $1 and $2 is : `expr $1 \* $2`"
}
div()
{
	echo "Division of numbers $1 and $2 is : `expr $1 / $2`"
}

echo "Please select operation choic to perform"
echo -e "1. Addition \n2. Subtraction \n3. Multiplication \n4. Division\n5. Exit"
read option
if [ $option -eq 5 ]
then
	exit 0
else
	echo "Enter number1:"
	read a
	echo "Enter number2"
	read b
	case "$option" in
		1)sum $a $b;;
		2)subtract $a $b;;
		3)multiply $a $b;;
		4)div $a $b;;
		*)echo "Sorry input not recognized";;
	esac
	
fi


