package com.engagepoint.university.admincentre;

public class ConsoleInputString {
    private String firstArg;
    private String secondArg;
    private String thirdArg;
    private String fourthArg;
    private String fifthArg;
    private int length;

    ConsoleInputString(String... args) {
        length = args.length;
        if (length > 5) {
            length = 5;
        }
        for (int i = 0; i < length; i++) {
            switch (i) {
                case 0:
                    this.firstArg = args[i];
                    break;
                case 1:
                    this.secondArg = args[i];
                    break;
                case 2:
                    this.thirdArg = args[i];
                    break;
                case 3:
                    this.fourthArg = args[i];
                    break;
                case 4:
                    this.fifthArg = args[i];
                    break;
                default:
            }
        }
    }

    public String getFirstArg() {
        return firstArg;
    }

    public String getSecondArg() {
        return secondArg;
    }

    public String getThirdArg() {
        return thirdArg;
    }

    public String getFourthArg() {
        return fourthArg;
    }

    public String getFifthArg() {
        return fifthArg;
    }

    public int getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "ConsoleInputString{" +
                "firstArg='" + firstArg + '\'' +
                ", secondArg='" + secondArg + '\'' +
                ", thirdArg='" + thirdArg + '\'' +
                ", fourthArg='" + fourthArg + '\'' +
                ", fifthArg='" + fifthArg + '\'' +
                ", length=" + length +
                '}';
    }
}
