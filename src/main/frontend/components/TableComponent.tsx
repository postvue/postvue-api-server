import styled from "styled-components";
import React, {useEffect} from "react";

interface TableComponentProps<T extends Record<string, any> | string[]> {
    headers?: string[]; // ✅ 헤더를 외부에서 받을 수 있도록 추가
    contentList: T[];
    onRowClick?: (row: Record<string, any> | string[]) => void;
}

const TableComponent = <T extends Record<string, any> | string[]>({ headers, contentList, onRowClick }: TableComponentProps<T>) => {
    if (contentList.length === 0) return null;

    const isStringArray = Array.isArray(contentList[0]);

    // ✅ 헤더가 props로 제공되지 않은 경우, contentList에서 자동 추출
    const tableHeaders = headers && headers.length > 0
        ? headers
        : isStringArray
            ? (contentList[0] as string[])
            : (Object.keys(contentList[0]) as Array<keyof T>);

    const bodyData = isStringArray ? contentList.slice(1) : contentList;

    return (
        <TableWrapper>
            <TableContainer>
                <TableHead>
                    <TableHeadCol>
                        {tableHeaders.map((col, index) => (
                            <TableHeadTh key={String(col)}>{String(col)}</TableHeadTh>
                        ))}
                    </TableHeadCol>
                </TableHead>
                <tbody>
                {bodyData.map((row, rowIndex) => (
                    <TableBodyTr
                        key={rowIndex}
                        onClick={() => onRowClick?.(isStringArray ? (row as string[]) : row)} // ✅ 객체 데이터 유지
                    >
                        {isStringArray
                            ? (row as string[]).map((col, colIndex) => (
                                <TableHeadTd key={colIndex}>{col}</TableHeadTd>
                            ))
                            : (tableHeaders as Array<keyof T>).map((key) => (
                                <TableHeadTd key={String(key)}>
                                    {typeof row[key] === "boolean"
                                        ? row[key] ? "✅ Yes" : "❌ No"
                                        : row[key] !== null && row[key] !== undefined
                                            ? String(row[key])
                                            : ""}
                                </TableHeadTd>
                            ))}
                    </TableBodyTr>
                ))}
                </tbody>
            </TableContainer>
        </TableWrapper>
    );
};

const TableWrapper = styled.div`
  width: 100dvw;
  overflow-x: auto; /* 가로 스크롤 허용 */
`;

const TableContainer = styled.table`
  border-collapse: collapse;
  margin: 25px 0;
  font-size: 0.9em;
  font-family: sans-serif;
  min-width: 400px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.15);
`;

const TableHead = styled.thead`
  background-color: #54A1D9;
  color: #ffffff;
  text-align: left;
`;

const TableHeadCol = styled.tr`
  background-color: #54A1D9;
  color: #ffffff;
  text-align: left;
`;

const TableBodyTr = styled.tr`
  border-bottom: 1px solid #dddddd;
  &:nth-of-type(even) {
    background-color: #f3f3f3;
  }
  &:last-of-type {
    border-bottom: 2px solid rgb(209 235 255);
  }
  cursor: pointer;

  transition: background 0.2s ease-in-out;
  @media (hover: hover) {
    &:hover {
      background: rgba(0, 0, 0, 0.2);
    }
  }

  @media (hover: none) {
    &:active {
      background: rgba(0, 0, 0, 0.2);
    }
  }
`;

const TableHeadTh = styled.th`
  padding: 12px 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 500px;
`;

const TableHeadTd = styled.td`
  padding: 12px 15px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 500px;
`;

export default TableComponent;
