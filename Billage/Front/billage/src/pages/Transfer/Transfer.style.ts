import styled from 'styled-components';
import theme from '/src/themes';

export const TranInputDiv = styled.div`
    width: 100%;
    display: flex;
    flex-direction: column;
    margin-bottom: 10px;
`;

export const TranInputTitle = styled.div`
    font-size: ${theme.fontSize.DF_16};
    font-weight: 800;
    width: 90%;
    padding: 0% 5% 0% 5%;
`;

export const StyledCheckbox = styled.input`
    font-size: 16px;
`;

export const SmallButtonsContainer = styled.div`
    display: flex;
    width: 90%;
    justify-content: flex-end;
`;

export const ButtonContainer = styled.div`
    position: relative;
    width: 94%;
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    bottom: 0px;
`;