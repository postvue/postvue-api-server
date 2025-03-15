export const isValidString = (str: string): boolean => {
    return str !== null && !/^\s*$/.test(str);
};