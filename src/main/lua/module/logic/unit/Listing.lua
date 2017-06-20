local unit_listing = {}

unit_listing.rarity_table = {
    common          = "C",
    common_plus     = "C+",
    uncommon        = "U",
    uncommon_plus   = "U+",
    rare            = "R",
    rare_plus       = "R+",
    ultrarare       = "UR",
    ultrarare_plus  = "UR+",
    legendary       = "L"
}

-- create a big table of all known units
function unit_listing.list () 
    return unit_listing.list_all_elements()
end


function unit_listing.list_element ()
    local title_obj = mw.title.getCurrentTitle()
    
    local t = mw.text.split(title_obj["text"],"/")
    local element_name = t[2]

    return unit_listing.list_one_element(element_name)
end


function unit_listing.spairs(t, order)
    -- collect the keys
    local keys = {}
    for k in pairs(t) do keys[#keys+1] = k end

    -- if order function given, sort by it by passing the table and keys a, b,
    -- otherwise just sort the keys 
    if order then
        table.sort(keys, function(a,b) return order(t, a, b) end)
    else
        table.sort(keys)
    end

    -- return the iterator function
    local i = 0
    return function()
        i = i + 1
        if keys[i] then
            return keys[i], t[keys[i]]
        end
    end
end
-- create the table headers with options
function unit_listing.start_table() 
    return  [====[
{| border="0" cellpadding="1" cellspacing="1" class="article-table sortable" 
! scope="col"|ID
! scope="col" class="unsortable" |Bestiary Icon
! scope="col"|Name
! scope="col"|Elem. 
! scope="col"|Rar.
! scope="col"|Skill
! scope="col"|Leader Skill
! scope="col"|Power
! scope="col"|HP
! scope="col"|DP
! scope="col"|Skill Charge
! scope="col"|Initiative]====]
end

-- close the table
function unit_listing.end_table() 
    return "|}"
end

-- list all units for every element
function unit_listing.list_all_elements() 
    
    local units_data = {}
    local temp_data =mw.loadData("Module:Data/Units/Fire" )
    for k,v in pairs(temp_data) do units_data[k] = v end
    
    temp_data = mw.loadData("Module:Data/Units/Light" )
    for k,v in pairs(temp_data) do units_data[k] = v end
    
    temp_data = mw.loadData("Module:Data/Units/Shadow" )
    for k,v in pairs(temp_data) do units_data[k] = v end
    
    temp_data = mw.loadData("Module:Data/Units/Water" )
    for k,v in pairs(temp_data) do units_data[k] = v end
    
    temp_data = mw.loadData("Module:Data/Units/Wood" )
    for k,v in pairs(temp_data) do units_data[k] = v end
    
    
    local disp = {}
    table.insert(disp,unit_listing.start_table())

    for unit_code, unit_data in unit_listing.spairs(units_data,function(t,a,b) return t[a]["bestiary"] < t[b]["bestiary"] end) do

        if unit_code then
            table.insert(disp, unit_listing.list_one_unit(unit_data))
        end
    end
    table.insert(disp,unit_listing.end_table())
    return table.concat(disp, "\n")
end



function unit_listing.list_one_element(element_name) 
    local units_data = mw.loadData("Module:Data/Units/"..element_name )
--    local temp_data =mw.loadData("Module:Data/Units/"..element_name )
--    for k,v in pairs(temp_data) do units_data[k] = v end


    local disp = {}

    table.insert(disp,unit_listing.start_table())

    for unit_code, unit_data in unit_listing.spairs(units_data,function(t,a,b) return t[a]["bestiary"] < t[b]["bestiary"] end) do
        if unit_code then
             table.insert(disp, unit_listing.list_one_unit(unit_data))
        end
    end
    table.insert(disp,unit_listing.end_table())
    return table.concat(disp, "\n")
end


-- create one table row for the unit data provided.
function unit_listing.list_one_unit(unit_data)
    local unit_number             = unit_data["bestiary"]
    local unit_code               = unit_data["codename"]
    local unit_name               = unit_data["nameen"]
    local unit_maxdp              = unit_data["maxdp"]
    local unit_maxhp              = unit_data["maxhp"]
    local unit_initiative         = unit_data["initiative"]
    local unit_skillcharge        = unit_data["skillcharge"]
    local unit_element            = unit_data["element"]
    local unit_rarity             = unit_data["rarity"]
    local unit_power              = unit_data["power"]
    local unit_skillname          = unit_data["skillname"]
    local unit_leaderskillname    = unit_data["leaderskillname"]
    
    local ph_idx, ph_end = string.find(unit_code,"placeholder")
    if ph_idx then
        return unit_listing.build_placeholder_row(unit_number)
    else
        return unit_listing.build_row_starter()
            .. unit_listing.build_colored_cell(unit_number,unit_element)
            .. unit_listing.build_icon_cell(unit_code,unit_element)
            .. unit_listing.build_name_cell(unit_element,unit_code,unit_name)
            .. unit_listing.build_element_cell(unit_element)
            .. unit_listing.build_rarity_cell(unit_rarity,unit_element )
            .. unit_listing.build_skill_cell(unit_code,unit_skillname,'Basic',unit_element)
            .. unit_listing.build_skill_cell(unit_code,unit_leaderskillname,'Leader',unit_element)
            .. unit_listing.build_colored_cell(unit_power,unit_element)
            .. unit_listing.build_colored_cell(unit_maxhp,unit_element)
            .. unit_listing.build_colored_cell(unit_maxdp,unit_element)
            .. unit_listing.build_colored_cell(unit_skillcharge,unit_element)
            .. unit_listing.build_colored_cell(string.gsub(unit_initiative,",","%."),unit_element)
    end
end

   
function unit_listing.build_placeholder_row(unit_number)
    return "\n|- \n|" .. unit_number .. "\n|\n|\n|\n|\n|\n|\n|\n|\n|\n|\n|\n|"
end

function unit_listing.build_row_starter()
    return "\n|- "
end

function unit_listing.build_colored_cell(celldata, unit_element)
    local adata = celldata or ""
    return "\n| class='".. unit_element .. "'| " .. adata
end

function unit_listing.build_basic_cell(celldata)
    local adata = celldata or ""
    return "\n| " .. adata
end

function unit_listing.build_icon_cell(codename,unit_element)
    return "\n| class='".. unit_element .. "'  | [[File:Unit_" .. codename .. "_card.png|thumb|center|50px|link=Card/"..unit_element.."/"..codename.."]]"
end

function unit_listing.build_name_cell(unit_element,unit_code,unit_name)
    return "\n| class='".. unit_element .. "' | <span style='font-weight: bold;'>[[Card/".. unit_element .. "/" .. unit_code .. "|" .. unit_name .."]]</span>"
end

function unit_listing.build_element_cell(unit_element)
    return "\n| class='".. unit_element .. "' | [[Card/" .. unit_element.. "|".. unit_element .. "]]"
end

function unit_listing.build_rarity_cell(rarity,unit_element)
    local rare = unit_listing.rarity_table[rarity] or ""
    return "\n|  class='".. unit_element .. "' | " .. rare
end

function unit_listing.build_skill_code(skill_name)
    local data = string.gsub(skill_name," ","_")
    data = string.gsub(data,"%-","_")
    data = string.gsub(data,"'","_")
    return string.gsub(data,"__","_")
end

function unit_listing.build_skill_cell(unit_code,skill_name, skills_data_type,unit_element)
    local skill_code;
    local displayed = "\n|  class='".. unit_element .. "' | "
    if skill_name and string.len(skill_name) > 0 then
        local skills_data = mw.loadData("Module:Data/Skills/" .. skills_data_type)
        skill_code = unit_listing.build_skill_code(skill_name)

        if skill_code  and string.len(skill_code) > 0 then
            local t = skills_data[skill_code]
            if t then
                displayed = displayed .. "[[Skill/" .. skills_data_type .."/".. skill_code .. "|" .. skill_name .. "]]"     
            else
                displayed = displayed .. " erratic skill code : ".. skill_code
            end
        else
             displayed = displayed .. " erratic skill code : ".. skill_code
        end
    else
        displayed = "\n|  class='".. unit_element .. "' style='text-align: center;' | - "  
    end
    return displayed

end

function unit_listing.build_skill_description(skill_data)
    return string.gsub(string.gsub(skill_data," ","&nbsp;"),"%.&nbsp;","%.\n<br/>")
end


function unit_listing.buid_description_overlay(unit_code,skill_data)
    return  "<br/><div class=\"mw-customtoggle-" .. unit_code .. " wikia-menu-button\">description</div> \n<div class=\"wikitable mw-collapsible mw-collapsed\" id=\"mw-customcollapsible-".. unit_code .. "\">\n" .. unit_listing.build_skill_description(skill_data) .. "\n</div>"
end



return unit_listing
